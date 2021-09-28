package com.example.gr34_in2000_v21.di

import android.content.Context
import com.example.gr34_in2000_v21.data.local.CacheDatabase
import com.example.gr34_in2000_v21.data.local.PersistentDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * LEGG TIL NYE DEPENDENCIES HER
 * Hvis du ikke vet hva de er, Hilt dokumentasjonen kan hjelpe: https://dagger.dev/hilt/modules
 *
 */
@Module(includes = [MetAlertsModule::class, GMSClientsModule::class, GeoNorgeModule::class])
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideCache(@ApplicationContext appContext: Context) = CacheDatabase.create(appContext)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        PersistentDatabase.create(appContext)
}