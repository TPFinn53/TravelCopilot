package com.example.travelcopilot.di

import com.example.travelcopilot.core.context.ContextProvider
import com.example.travelcopilot.core.engine.CopilotEngine
import com.example.travelcopilot.core.state.TripStateMachine
import com.example.travelcopilot.data.repository.ChatRepository
import com.example.travelcopilot.data.repository.TripEventRepository
import com.example.travelcopilot.data.repository.TripRepository
import com.example.travelcopilot.infra.location.LocationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    // 🧠 STATE MACHINE
    @Provides
    @Singleton
    fun provideTripStateMachine(): TripStateMachine {
        return TripStateMachine()
    }

    // 🧭 CONTEXT PROVIDER
    @Provides
    @Singleton
    fun provideContextProvider(
        tripRepository: TripRepository,
        eventRepository: TripEventRepository,
        chatRepository: ChatRepository,
        locationProvider: LocationProvider

    ): ContextProvider {
        return ContextProvider(
            tripRepository = tripRepository,
            eventRepository = eventRepository,
            chatRepository = chatRepository,
            locationProvider = locationProvider
        )
    }
}