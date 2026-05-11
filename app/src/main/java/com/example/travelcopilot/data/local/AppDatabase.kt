package com.example.travelcopilot.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.travelcopilot.data.local.dao.ChatDao
import com.example.travelcopilot.data.local.dao.TripDao
import com.example.travelcopilot.data.local.dao.TripEventDao
import com.example.travelcopilot.data.local.entity.ChatMessageEntity
import com.example.travelcopilot.data.local.entity.TripEventEntity
import com.example.travelcopilot.data.local.entity.TripEntity


@Database(
    entities = [
        TripEntity::class,   // ✅ correct
        TripEventEntity::class,
        ChatMessageEntity::class,
        MemoryItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun tripEventDao(): TripEventDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE trips ADD COLUMN state TEXT NOT NULL DEFAULT 'IDLE'")
                db.execSQL("ALTER TABLE trips ADD COLUMN startTime INTEGER")
                db.execSQL("ALTER TABLE trips ADD COLUMN endTime INTEGER")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "travelcopilot_db"
                )
                    .fallbackToDestructiveMigration() // Replace with real migrations later
                    //.addMigrations(MIGRATION_3_4) // this is the real migrations replacement - uncomment later
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
