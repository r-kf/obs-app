package com.example.gr34_in2000_v21.di

import com.example.gr34_in2000_v21.data.local.PersistentDatabase
import com.example.gr34_in2000_v21.data.remote.GeoNorgeDataSource
import com.example.gr34_in2000_v21.data.remote.GeoNorgeService
import com.example.gr34_in2000_v21.data.repository.GeoNorgeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Modul for alt med MetAlerts å gjøre
 */
@Module
@InstallIn(SingletonComponent::class)
object GeoNorgeModule {
    @Singleton
    @Provides
    @Named("geonorge-retrofit")
    fun provideGeoNorgeRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(logging)
        return Retrofit.Builder()
            .baseUrl("https://ws.geonorge.no/kommuneinfo/v1/")
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideGeoNorgeService(@Named("geonorge-retrofit") retrofit: Retrofit): GeoNorgeService =
        retrofit.create(GeoNorgeService::class.java)

    @Singleton
    @Provides
    fun provideGeoNorgeRepository(
        remote: GeoNorgeDataSource,
        database: PersistentDatabase
    ) =
        GeoNorgeRepository(remote = remote, database = database)

    @Singleton
    @Provides
    fun provideGeoNorgeDataSource(service: GeoNorgeService) = GeoNorgeDataSource(service)
}