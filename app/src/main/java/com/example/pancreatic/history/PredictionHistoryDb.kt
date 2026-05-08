package com.example.pancreatic.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PredictionRecord::class],
    version = 1,
    exportSchema = false,
)
abstract class PredictionHistoryDb : RoomDatabase() {
    abstract fun predictionRecordDao(): PredictionRecordDao

    companion object {
        @Volatile private var INSTANCE: PredictionHistoryDb? = null

        fun get(context: Context): PredictionHistoryDb {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PredictionHistoryDb::class.java,
                    "prediction_history.db",
                ).build().also { INSTANCE = it }
            }
        }
    }
}

