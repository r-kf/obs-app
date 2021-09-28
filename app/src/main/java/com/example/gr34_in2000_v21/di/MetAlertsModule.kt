package com.example.gr34_in2000_v21.di

import com.example.gr34_in2000_v21.data.local.CacheDatabase
import com.example.gr34_in2000_v21.data.remote.MetAlertsDataSource
import com.example.gr34_in2000_v21.data.remote.MetAlertsService
import com.example.gr34_in2000_v21.data.repository.MetAlertsRepository
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Modul for alt med MetAlerts å gjøre
 */
@Module
@InstallIn(SingletonComponent::class)
object MetAlertsModule {
    @Singleton
    @Provides
    fun provideMetAlertsRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(logging)
        val tikXml =
            TikXml.Builder().exceptionOnUnreadXml(false).build() // No need to parse unneeded data
        return Retrofit.Builder()
            .baseUrl("https://in2000-apiproxy.ifi.uio.no/weatherapi/metalerts/")
            .client(okHttpClient.build())
            .addConverterFactory(TikXmlConverterFactory.create(tikXml))
            .build()
    }

    @Provides
    fun provideMetAlertsService(retrofit: Retrofit): MetAlertsService =
        retrofit.create(MetAlertsService::class.java)

    @Singleton
    @Provides
    fun provideMetAlertsRepository(
        remote: MetAlertsDataSource,
        database: CacheDatabase
    ) =
        MetAlertsRepository(remote = remote, database = database)

    @Singleton
    @Provides
    fun provideMetAlertsDataSource(service: MetAlertsService) = MetAlertsDataSource(service)
}