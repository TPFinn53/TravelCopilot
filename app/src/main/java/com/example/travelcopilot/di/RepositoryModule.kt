package com.example.travelcopilot.di

import com.example.travelcopilot.core.state.TripStateMachine
import com.example.travelcopilot.data.local.dao.ChatDao
import com.example.travelcopilot.data.local.dao.TripDao
import com.example.travelcopilot.data.local.dao.TripEventDao
import com.example.travelcopilot.data.repository.ChatRepository
import com.example.travelcopilot.data.repository.TripEventRepository
import com.example.travelcopilot.data.repository.TripRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // 🚗 Trip Repository
    @Provides
    @Singleton
    fun provideTripRepository(
        tripDao: TripDao,
        stateMachine: TripStateMachine
    ): TripRepository {
        return TripRepository(tripDao)
    }

    // 📍 Trip Event Repository
    @Provides
    @Singleton
    fun provideTripEventRepository(
        tripEventDao: TripEventDao
    ): TripEventRepository {
        return TripEventRepository(tripEventDao)
    }

    // 💬 Chat Repository
    @Provides
    @Singleton
    fun provideChatRepository(
        chatDao: ChatDao
    ): ChatRepository {
        return ChatRepository(chatDao)
    }
}