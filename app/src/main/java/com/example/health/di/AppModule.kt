package com.example.health.di

import android.content.Context
import com.example.health.data.local.JsonDataSource
import com.example.health.data.local.UserPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // No manual providers needed for classes with @Inject constructor
}
