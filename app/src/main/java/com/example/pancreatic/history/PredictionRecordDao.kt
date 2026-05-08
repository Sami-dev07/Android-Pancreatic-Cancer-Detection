package com.example.pancreatic.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PredictionRecordDao {
    @Query("SELECT * FROM prediction_records ORDER BY createdAtEpochMs DESC")
    fun observeAll(): Flow<List<PredictionRecord>>

    @Query("SELECT * FROM prediction_records WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<PredictionRecord?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(record: PredictionRecord): Long

    @Query("DELETE FROM prediction_records")
    suspend fun clearAll()
}

