package com.example.examine.data.remote.models

import com.google.gson.annotations.SerializedName

data class Answer(
    @SerializedName("test_question_id") val testQuestionId: Int,
    @SerializedName("answer") var answer: String
)

data class AnswerPayload(
    @SerializedName("code") val code: String,
    @SerializedName("answers") val answers: List<Answer>
)