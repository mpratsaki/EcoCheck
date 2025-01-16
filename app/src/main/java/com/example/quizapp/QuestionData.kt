package com.example.quizapp

data class QuestionData (
    var id:Int,
    var question:String,
    var option_one:String,
    var option_two:String,
    var option_three:String,
    var option_four:String,
    var option_five:String,
    val points1: Int,
    val points2: Int,
    val points3: Int,
    val points4: Int,
    val points5: Int,
    var selectedOption: Int = 0
)
