package com.islaharper.jetpacktrivia.repository

import android.util.Log
import com.islaharper.jetpacktrivia.data.DataorException
import com.islaharper.jetpacktrivia.model.QuestionItem
import com.islaharper.jetpacktrivia.network.QuestionAPI
import javax.inject.Inject

class QuestionRepository @Inject constructor(private val questionAPI: QuestionAPI) {
    private val dataOrException = DataorException<ArrayList<QuestionItem>, Boolean, Exception>()

    suspend fun getAllQuestions(): DataorException<ArrayList<QuestionItem>, Boolean, Exception> {
        try {
            dataOrException.loading = true
            dataOrException.data = questionAPI.getAllQuestions()
            if (dataOrException.data.toString().isNotEmpty()) {
                dataOrException.loading = false
            }
        } catch (e: Exception) {
            dataOrException.e = e
            dataOrException.loading = false
            Log.d("QUESTION REPOSITORY", "Exception: ${e.localizedMessage}")
        }
        return dataOrException
    }
}