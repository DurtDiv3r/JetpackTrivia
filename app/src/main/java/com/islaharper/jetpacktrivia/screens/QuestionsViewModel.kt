package com.islaharper.jetpacktrivia.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islaharper.jetpacktrivia.data.DataorException
import com.islaharper.jetpacktrivia.model.QuestionItem
import com.islaharper.jetpacktrivia.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(private val repository: QuestionRepository): ViewModel() {

    val data: MutableState<DataorException<ArrayList<QuestionItem>, Boolean, Exception>> = mutableStateOf(DataorException(null, true, Exception("")))

    init {
        getAllQuestions()
    }
    private fun getAllQuestions() {
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllQuestions()

            if (data.value.data.toString().isNotEmpty()) {
                data.value.loading = false
            }
        }
    }
}