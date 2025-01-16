package com.example.quizapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result.*

class Result : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var Name: String = ""
    private lateinit var vibrator: Vibrator
    private var isVibrationEnabled: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        Name = intent.getStringExtra(setData.name) ?: ""

        val score = intent.getStringExtra(setData.score)

        congo.text = "Μόλις ολοκληρώσατε το τεστ!\nΣυμπληρώσατε όλες τις απαντήσεις του ερωτηματολογίου!"

        score?.let { scr ->
            scoreTextView.text = "Score: $scr/500"
        }

        sharedPreferences = getSharedPreferences("quizapp", MODE_PRIVATE)
        val maxScore = sharedPreferences.getInt("max_score", 0)
        val newMaxScore = maxScore + (score?.toIntOrNull() ?: 0)

        val editor = sharedPreferences.edit()
        editor.putInt("max_score", newMaxScore)
        editor.apply()

        button_finish.setOnClickListener {
            if (isVibrationEnabled) {
                vibrateDevice()
            }
            val intent = Intent(this, ControlMenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        redirectButton.setOnClickListener {
            if (isVibrationEnabled) {
                vibrateDevice()
            }
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra(setData.name, Name)
            intent.putExtra("fromResult", true)
            intent.putExtra(setData.score, score)
            intent.putExtra("previousActivity", this.javaClass.name)
            startActivity(intent)
            finish()
        }
    }

    private fun vibrateDevice() {
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // Deprecated in API 26
            vibrator.vibrate(100)
        }
    }
}
