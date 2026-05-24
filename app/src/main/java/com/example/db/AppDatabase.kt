package com.example.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Database
import androidx.room.RoomDatabase

@Entity(tableName = "match_history")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val durationSeconds: Int,
    val winnerSide: String,
    val playersCount: Int
)

@Dao
interface MatchDao {
    @Query("SELECT * FROM match_history ORDER BY date DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)
}

@Database(entities = [MatchEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
}
