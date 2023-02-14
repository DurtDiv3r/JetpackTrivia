package com.islaharper.jetpacktrivia.component

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islaharper.jetpacktrivia.model.QuestionItem
import com.islaharper.jetpacktrivia.screens.QuestionsViewModel
import com.islaharper.jetpacktrivia.util.AppColours

@Composable
fun Questions(viewModel: QuestionsViewModel) {
    val questions = viewModel.data.value.data?.toMutableList()
    val questionIndex = remember {
        mutableStateOf(0)
    }
    val hiddenButton = remember {
        mutableStateOf(false)
    }

    if (viewModel.data.value.loading == true) {
        Log.d("MainActivity", "Loading questions...")
        CircularProgressIndicator()
    } else {
        Log.d("MainActivity", "Loading done...")
        val question = try {
            questions?.get(questionIndex.value)
        } catch (e: Exception) {
            null
        }
        questions?.let {
            QuestionsDisplay(
                question = question!!,
                questionIndex = questionIndex,
                viewModel = viewModel
            ) {
                questionIndex.value = questionIndex.value + 1
            }
        }
    }
    Log.d("MainActivity", "Questions: ${questions?.size}")
}

//@Preview
@Composable
fun QuestionsDisplay(
    question: QuestionItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionsViewModel,
    onNextClicked: (Int) -> Unit = {}
) {
    val choicesState = remember(question) {
        question.choices?.toMutableList()
    }

    val answerState = remember(question) {
        mutableStateOf<Int?>(null)
    }

    val correctAnswerState = remember(question) {
        mutableStateOf<Boolean?>(null)
    }

    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            answerState.value = it
            correctAnswerState.value = choicesState?.get(it) == question.answer
        }
    }

    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(), color = AppColours.mDarkPurple
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start
        ) {
            if (questionIndex.value >= 3) {
                ShowScoreProgress(score = questionIndex.value)
            }
            QuestionProgress(counter = questionIndex.value, viewModel.totalQuestionCount())
            DrawDottedLine(path = pathEffect)

            Column {
                question.question?.let {
                    Text(
                        text = it,
                        modifier = Modifier
                            .padding(6.dp)
                            .align(alignment = Alignment.Start)
                            .fillMaxHeight(0.3f),
                        fontSize = 17.sp,
                        color = AppColours.mOffWhite,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                }
                choicesState?.forEachIndexed { answerIndex, answerText ->
                    Row(
                        modifier = Modifier
                            .padding(3.dp)
                            .fillMaxWidth()
                            .height(45.dp)
                            .border(
                                width = 4.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppColours.mOffDarkPurple,
                                        AppColours.mOffDarkPurple
                                    )
                                ),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .clip(
                                RoundedCornerShape(
                                    topStartPercent = 50,
                                    topEndPercent = 50,
                                    bottomStartPercent = 50,
                                    bottomEndPercent = 50
                                )
                            )
                            .background(color = Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (answerState.value == answerIndex),
                            onClick = {
                                updateAnswer(answerIndex)
                            },
                            modifier = Modifier.padding(start = 16.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = if (correctAnswerState.value == true && answerIndex == answerState.value) {
                                    Color.Green.copy(alpha = 0.2f)
                                } else {
                                    Color.Red.copy(alpha = 0.2f)
                                }
                            )
                        )
                        if (answerText != null) {
                            val annotatedString = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Light,
                                        color = if (correctAnswerState.value == true && answerIndex == answerState.value) {
                                            Color.Green
                                        } else if (correctAnswerState.value == false && answerIndex == answerState.value) {
                                            Color.Red
                                        } else {
                                            AppColours.mOffWhite
                                        }, fontSize = 17.sp
                                    )
                                ) {
                                    append(answerText)
                                }
                            }

                            Text(text = annotatedString, modifier = Modifier.padding(6.dp))
                        }
                    }
                }
                Button(
                    onClick = { onNextClicked(questionIndex.value) },
                    modifier = Modifier
                        .padding(3.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(25.dp), colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColours.mLightBlue
                    )
                ) {
                    Text(
                        text = "Next",
                        modifier = Modifier.padding(4.dp),
                        color = AppColours.mOffWhite,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionProgress(counter: Int = 10, totalQuestions: Int = 100) {
    Text(text = buildAnnotatedString {
        withStyle(style = ParagraphStyle(textIndent = TextIndent.None)) {
            withStyle(
                style = SpanStyle(
                    color = AppColours.mLightGrey,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            ) {
                append("Question $counter/$totalQuestions")
            }
        }
    }, Modifier.padding(20.dp))
}

@Composable
fun DrawDottedLine(path: PathEffect) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = AppColours.mLightGrey,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = path
        )
    }
}

@Preview
@Composable
fun ShowScoreProgress(score: Int = 10) {
    val gradient = Brush.linearGradient(listOf(Color(0xFFF95075), Color(0xFFBE6BE5)))
    val progressPercentage = remember(score) {
        mutableStateOf(score * 0.005f)
    }
    Row(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .height(45.dp)
            .border(
                width = 4.dp, brush = Brush.linearGradient(
                    colors = listOf(
                        AppColours.mLightPurple, AppColours.mLightPurple
                    )
                ), shape = RoundedCornerShape(34.dp)
            )
            .clip(
                RoundedCornerShape(
                    topStartPercent = 50,
                    topEndPercent = 50,
                    bottomStartPercent = 50,
                    bottomEndPercent = 50
                )
            )
            .background(Color.Transparent), verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            contentPadding = PaddingValues(1.dp),
            onClick = { },
            modifier = Modifier
                .fillMaxWidth(progressPercentage.value)
                .background(brush = gradient), enabled = false, elevation = null, colors = buttonColors(
                backgroundColor = Color.Transparent, disabledBackgroundColor = Color.Transparent
                )
        ) {

        }

    }
}