package com.example.examine.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.examine.data.remote.models.Answer
import com.example.examine.data.remote.models.TestQuestionsItem
import com.example.examine.databinding.ItemQuestionsRowBinding

class TestQuestionsAdapter : RecyclerView.Adapter<TestQuestionsAdapter.ViewHolder>() {
    private val listTest: ArrayList<TestQuestionsItem> = arrayListOf()
    private val listAnswer: ArrayList<Answer> = arrayListOf()

    fun setList(testList: ArrayList<TestQuestionsItem>) {
        this.listTest.clear()
        this.listAnswer.clear()

        this.listTest.addAll(testList)

        for (test in listTest) {
            listAnswer.add(
                Answer(
                    testQuestionId = test.id!!,
                    answer = ""
                )
            )
        }

        notifyDataSetChanged()
    }

    fun getAllAnswer() = listAnswer

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemQuestionsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listTest[position], position)
    }

    override fun getItemCount() = listTest.size

    inner class ViewHolder(private var binding: ItemQuestionsRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(test: TestQuestionsItem, position: Int) {
            with(binding) {
                tvQuestions.text = test.question
                edAnswer.addTextChangedListener { answer ->
                    listAnswer[position].answer = answer.toString()
                }
            }
        }
    }
}