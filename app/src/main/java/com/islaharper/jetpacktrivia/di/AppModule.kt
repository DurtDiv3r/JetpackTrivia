package com.islaharper.jetpacktrivia.di

import com.islaharper.jetpacktrivia.network.QuestionAPI
import com.islaharper.jetpacktrivia.repository.QuestionRepository
import com.islaharper.jetpacktrivia.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideQuestionRepository(api: QuestionAPI) = QuestionRepository(api)

    @Singleton
    @Provides
    fun provideQuestionApi(): QuestionAPI {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuestionAPI::class.java)
    }
}