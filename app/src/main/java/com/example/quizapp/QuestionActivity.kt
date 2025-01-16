package com.example.quizapp

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.settings_activity.previousbutton

class QuestionActivity : AppCompatActivity() {

    private var Name: String? = null
    private var score: Int = 0
    private var currentPosition: Int = 0
    private var questionList: ArrayList<QuestionData>? = null
    private var selectedOptions: HashMap<Int, Int?> = HashMap()

    private lateinit var sharedPreferences: SharedPreferences
    private var isVibrationEnabled: Boolean = true
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        Name = intent.getStringExtra(setData.name)

        questionList = setData.getQuestion()
        currentPosition = 0

        sharedPreferences = getSharedPreferences("quizapp", MODE_PRIVATE)
        isVibrationEnabled = sharedPreferences.getBoolean("vibration_enabled", true)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        setQuestion()

        opt_1.setOnClickListener {
            selectedOptionStyle(opt_1, 1)
        }
        opt_2.setOnClickListener {
            selectedOptionStyle(opt_2, 2)
        }
        opt_3.setOnClickListener {
            selectedOptionStyle(opt_3, 3)
        }
        opt_4.setOnClickListener {
            selectedOptionStyle(opt_4, 4)
        }
        opt_5.setOnClickListener {
            selectedOptionStyle(opt_5, 5)
        }

        submit.setOnClickListener {
            val question = questionList!![currentPosition]

            if (selectedOptions[currentPosition] == null) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentPosition < questionList!!.size - 1) {
                currentPosition++
                setQuestion()
            } else {
                showConfirmationDialog()
            }

            if (isVibrationEnabled) {
                vibrateDevice()
            }
        }

        previousbutton.setOnClickListener {
            if (currentPosition > 0) {
                val previousQuestion = questionList!![currentPosition - 1]

                // Subtract points from the previous selection
                val previousSelectedOption = selectedOptions[currentPosition]
                if (previousSelectedOption != null) {
                    val previousPoints = when (previousSelectedOption) {
                        1 -> previousQuestion.points1
                        2 -> previousQuestion.points2
                        3 -> previousQuestion.points3
                        4 -> previousQuestion.points4
                        5 -> previousQuestion.points5
                        else -> 0
                    }
                    score -= previousPoints
                }

                currentPosition--
                setQuestion()
            } else {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure that you want to go back? All your progress will be lost")
                    .setPositiveButton("Yes") { _, _ ->
                        val intent = Intent(this, ControlMenuActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                dialog.show()
            }

            if (isVibrationEnabled) {
                vibrateDevice()
            }
        }

        if (intent.getBooleanExtra("fromResult", false)) {
            // Navigate to the last question if started from the Result Activity
            currentPosition = questionList!!.size - 1
            setQuestion()
        }

        settingsButton.setOnClickListener {
            if (isVibrationEnabled) {
                vibrateDevice()
            }
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("previousActivity", this.javaClass.name)
            startActivity(intent)
            finish()
        }
    }

    private fun setQuestion() {
        val question = questionList!![currentPosition]
        setOptionStyle()

        progress_bar.progress = currentPosition
        progress_bar.max = questionList!!.size
        progress_text.text = "${currentPosition + 1}/${questionList!!.size}"
        question_text.text = question.question
        opt_1.text = question.option_one
        opt_2.text = question.option_two
        opt_3.text = question.option_three
        opt_4.text = question.option_four
        opt_5.text = question.option_five

        val selectedOption = selectedOptions[currentPosition]
        if (selectedOption != null) {
            val selectedView = when (selectedOption) {
                1 -> opt_1
                2 -> opt_2
                3 -> opt_3
                4 -> opt_4
                5 -> opt_5
                else -> null
            }
            selectedView?.let {
                selectedOptionStyle(it, selectedOption)
            }
        } else {
            scoreee.text = "Score: $score" // Update score display
        }
    }

    private fun setOptionStyle() {
        val optionList: ArrayList<TextView> = arrayListOf(opt_1, opt_2, opt_3, opt_4, opt_5)

        for (op in optionList) {
            op.setTextColor(Color.parseColor("#555151"))
            op.background = ContextCompat.getDrawable(this, R.drawable.question_option)
            op.typeface = Typeface.DEFAULT
        }
    }

    private fun selectedOptionStyle(view: TextView, opt: Int) {
        setOptionStyle()

        val question = questionList!![currentPosition]

        val previousOption = selectedOptions[currentPosition]
        if (previousOption != null) {
            val previousPoints = when (previousOption) {
                1 -> question.points1
                2 -> question.points2
                3 -> question.points3
                4 -> question.points4
                5 -> question.points5
                else -> 0
            }
            score -= previousPoints // Subtract points from the previous selection
        }

        selectedOptions[currentPosition] = opt

        view.background = ContextCompat.getDrawable(this, R.drawable.selected_question_option)
        view.typeface = Typeface.DEFAULT_BOLD
        view.setTextColor(Color.parseColor("#000000"))

        val points = when (opt) {
            1 -> question.points1
            2 -> question.points2
            3 -> question.points3
            4 -> question.points4
            5 -> question.points5
            else -> 0
        }

        score += points // Add points for the current selection
        scoreee.text = "Score: $score" // Update score display
    }

    private fun showConfirmationDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure about your answers?")
            .setPositiveButton("Yes") { _, _ ->
                redirectToResultActivity()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun redirectToResultActivity() {
        val intent = Intent(this, Result::class.java)
        intent.putExtra(setData.name, Name.toString())
        intent.putExtra(setData.score, score.toString())
        intent.putExtra("total size", questionList!!.size.toString())
        startActivity(intent)
        finish()
    }

    private fun vibrateDevice() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // Deprecated in API 26
            vibrator.vibrate(100)
        }
    }
}
