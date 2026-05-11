package com.example.travelcopilot.di

import android.content.Context
import androidx.room.Room
import com.example.travelcopilot.data.local.AppDatabase
import com.example.travelcopilot.data.local.dao.TripDao
import com.example.travelcopilot.data.local.dao.TripEventDao
import com.example.travelcopilot.data.local.dao.ChatDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "travelcopilot_db"
        ).build()
    }

    @Provides fun provideTripDao(db: AppDatabase): TripDao = db.tripDao()
    @Provides fun provideTripEventDao(db: AppDatabase): TripEventDao = db.tripEventDao()
    @Provides fun provideChatDao(db: AppDatabase): ChatDao = db.chatDao()
}