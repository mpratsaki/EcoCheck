package com.example.quizapp

import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.VibrationUtil
import android.media.MediaPlayer
import kotlinx.android.synthetic.main.settings_activity.*
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var isVibrationEnabled: Boolean = true
    private var previousActivityName: String? = null
    private lateinit var mediaPlayer: MediaPlayer
    private var isMusicPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        mediaPlayer = MediaPlayer.create(this, R.raw.test)
        mediaPlayer.isLooping = true

        sharedPreferences = getSharedPreferences("quizapp", MODE_PRIVATE)
        previousActivityName = intent.getStringExtra("previousActivity")

        button_vibration.setOnClickListener {
            toggleVibration()
            updateVibrationButtonState()
            notifyOtherActivities()
        }

        button_home.setOnClickListener {
            if (isVibrationEnabled) {
                VibrationUtil.vibrate(this)
            }
            val intent = Intent(this, ControlMenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        button_music.setOnClickListener {
            if (isMusicPlaying) {
                mediaPlayer.pause()
                isMusicPlaying = false
                showToast("Music stopped")
            } else {
                mediaPlayer.start()
                isMusicPlaying = true
                showToast("Music is playing")
            }
        }

        previousbutton.setOnClickListener {
            if (isVibrationEnabled) {
                VibrationUtil.vibrate(this)
            }
            if (!previousActivityName.isNullOrEmpty()) {
                try {
                    val previousActivityClass = Class.forName(previousActivityName)
                    val intent = Intent(this, previousActivityClass)
                    startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                    // Handle the case when the previous activity class is not found
                    // For example, redirect to the MainActivity as a fallback
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            } else {
                // Handle the case when previousActivityName is null or empty
                // For example, redirect to the MainActivity as a fallback
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            finish() // Optional: Finish the current activity if you don't want to go back to it
        }

        button_help.setOnClickListener {
            if (isVibrationEnabled) {
                VibrationUtil.vibrate(this)
            }
            if (!previousActivityName.isNullOrEmpty()) {
                try {
                    val previousActivityClass = Class.forName(previousActivityName)
                    val intent = Intent(this, previousActivityClass)
                    startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                    // Handle the case when the previous activity class is not found
                    // For example, redirect to the MainActivity as a fallback
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            } else {
                // Handle the case when previousActivityName is null or empty
                // For example, redirect to the MainActivity as a fallback
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            finish() // O
        }

        resetButton.setOnClickListener {
            if (isVibrationEnabled) {
                VibrationUtil.vibrate(this)
            }
            resetData()
            val editor = sharedPreferences.edit()
            editor.putBoolean("form_filled", false)
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        isVibrationEnabled = sharedPreferences.getBoolean("vibration_enabled", true)
        updateVibrationButtonState()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun notifyOtherActivities() {
        val intent = Intent(ACTION_VIBRATION_CHANGED)
        intent.putExtra("vibration_enabled", isVibrationEnabled)
        sendBroadcast(intent)
    }

    private fun toggleVibration() {
        isVibrationEnabled = !isVibrationEnabled
        val editor = sharedPreferences.edit()
        editor.putBoolean("vibration_enabled", isVibrationEnabled)
        editor.apply()
        updateVibrationButtonState()

        if (isVibrationEnabled) {
            VibrationUtil.vibrate(this)
        }
    }

    private fun updateVibrationButtonState() {
        if (isVibrationEnabled) {
            button_vibration.text = "Vibration: On"
        } else {
            button_vibration.text = "Vibration: Off"
        }
    }

    private fun resetData() {
        val editor = sharedPreferences.edit()
        editor.putInt("max_score", 0)
        // Reset other data fields if needed
        editor.apply()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ACTION_VIBRATION_CHANGED = "com.example.quizapp.ACTION_VIBRATION_CHANGED"
    }
}
