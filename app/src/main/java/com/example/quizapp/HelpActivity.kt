package com.example.quizapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.VibrationUtil
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var isVibrationEnabled: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        sharedPreferences = getSharedPreferences("quizapp", MODE_PRIVATE)

        isVibrationEnabled = sharedPreferences.getBoolean("vibration_enabled", true)

        previousbutton.setOnClickListener {
            if (isVibrationEnabled) {
                VibrationUtil.vibrate(this)
            }
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
