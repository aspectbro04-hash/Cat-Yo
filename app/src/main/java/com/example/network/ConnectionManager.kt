package com.example.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.example.game.ActionType
import com.example.game.JoinRoomData
import com.example.game.NetworkPayload
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

class ConnectionManager(private val context: Context) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val SERVICE_TYPE = "_mafia._tcp."
    private var serverSocket: ServerSocket? = null
    
    private var isHost = false
    private var myId = ""
    
    private val clientSockets = mutableListOf<Socket>()
    private var hostSocket: Socket? = null
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val _payloads = MutableSharedFlow<NetworkPayload>()
    val payloads = _payloads.asSharedFlow()
    
    // Hosting
    fun startHost(port: Int = 0): Int {
        isHost = true
        serverSocket = ServerSocket(port)
        val actualPort = serverSocket!!.localPort
        
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = "MafiaRoom_${System.currentTimeMillis()}"
            serviceType = SERVICE_TYPE
            setPort(actualPort)
        }
        
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
                Log.d("ConnectionManager", "Service registered: ${NsdServiceInfo.serviceName}")
            }
            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
            override fun onServiceUnregistered(arg0: NsdServiceInfo) {}
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
        })
        
        scope.launch {
            try {
                while (true) {
                    val client = serverSocket!!.accept()
                    clientSockets.add(client)
                    listenToSocket(client)
                }
            } catch (e: Exception) {
                Log.e("ConnectionManager", "Server socket error", e)
            }
        }
        return actualPort
    }
    
    // Client
    fun discoverAndConnect(playerName: String, myPlayerId: String) {
        myId = myPlayerId
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {}
            override fun onServiceFound(service: NsdServiceInfo) {
                if (service.serviceType == SERVICE_TYPE) {
                    nsdManager.resolveService(service, object : NsdManager.ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
                        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                            connectToHost(serviceInfo.host, serviceInfo.port, playerName)
                        }
                    })
                }
            }
            override fun onServiceLost(service: NsdServiceInfo) {}
            override fun onDiscoveryStopped(serviceType: String) {}
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {}
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {}
        })
    }
    
    private fun connectToHost(host: InetAddress, port: Int, playerName: String) {
        scope.launch {
            try {
                val socket = Socket(host, port)
                hostSocket = socket
                listenToSocket(socket)
                
                // Send join payload
                val joinData = Json.encodeToString(JoinRoomData(playerName))
                val payload = NetworkPayload(ActionType.JOIN_ROOM, myId, joinData)
                sendPayload(payload)
            } catch (e: Exception) {
                Log.e("ConnectionManager", "Connection failed", e)
            }
        }
    }
    
    private fun listenToSocket(socket: Socket) {
        scope.launch {
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                while (true) {
                    val line = reader.readLine() ?: break
                    val payload = Json.decodeFromString<NetworkPayload>(line)
                    _payloads.emit(payload)
                }
            } catch (e: Exception) {
                 Log.e("ConnectionManager", "Socket read error", e)
            } finally {
                socket.close()
                clientSockets.remove(socket)
            }
        }
    }
    
    fun sendPayload(payload: NetworkPayload) {
        scope.launch {
            val msg = Json.encodeToString(payload) + "\n"
            if (isHost) {
                // Broadcast to all
                clientSockets.forEach { socket ->
                    try {
                        val writer = PrintWriter(socket.getOutputStream(), true)
                        writer.print(msg)
                        writer.flush()
                    } catch (e: Exception) {}
                }
                // emit locally for host
                _payloads.emit(payload)
            } else {
                // Send to host
                hostSocket?.let {
                    try {
                        val writer = PrintWriter(it.getOutputStream(), true)
                        writer.print(msg)
                        writer.flush()
                    } catch (e: Exception) {}
                }
            }
        }
    }
    
    fun processLocalHostPayload(payload: NetworkPayload) {
        if(isHost) {
            scope.launch {
                _payloads.emit(payload)
                sendPayload(payload)
            }
        }
    }
}
