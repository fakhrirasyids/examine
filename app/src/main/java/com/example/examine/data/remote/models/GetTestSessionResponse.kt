package com.example.examine.data.remote.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetTestSessionResponse(
	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

@Parcelize
data class TestQuestionsItem(

	@field:SerializedName("question")
	val question: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("test_id")
	val testId: String? = null
) : Parcelable

@Parcelize
data class Test(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("test_questions")
	val testQuestions: List<TestQuestionsItem?>? = null,

	@field:SerializedName("subject")
	val subject: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
): Parcelable


@Parcelize
data class TestSession(

	@field:SerializedName("date_start")
	val dateStart: String? = null,

	@field:SerializedName("teacher")
	val teacher: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("test")
	val test: Test? = null,

	@field:SerializedName("time_start")
	val timeStart: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("time_end")
	val timeEnd: String? = null,

	@field:SerializedName("class")
	val jsonMemberClass: String? = null,

	@field:SerializedName("test_id")
	val testId: String? = null
): Parcelable
