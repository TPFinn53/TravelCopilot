package com.example.travelcopilot.di

import android.content.Context
import com.example.travelcopilot.infra.speech.VoiceInputManager
import com.example.travelcopilot.infra.speech.VoiceOutputManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpeechModule {

    @Provides
    @Singleton
    fun provideVoiceInputManager(
        @ApplicationContext context: Context
    ): VoiceInputManager {
        return VoiceInputManager(context)
    }

    @Provides
    @Singleton
    fun provideVoiceOutputManager(
        @ApplicationContext context: Context
    ): VoiceOutputManager {
        return VoiceOutputManager(context)
    }
}