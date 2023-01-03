package com.islaharper.jetpacktrivia.network

import com.islaharper.jetpacktrivia.model.Question
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface QuestionAPI {
    @GET("world.json")
    suspend fun getAllQuestions(): Question
}