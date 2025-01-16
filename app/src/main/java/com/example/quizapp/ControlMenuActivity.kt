    package com.example.quizapp

    import android.content.BroadcastReceiver
    import android.content.Context
    import android.content.Intent
    import android.content.IntentFilter
    import android.content.SharedPreferences
    import android.net.Uri
    import android.os.Bundle
    import android.widget.Button
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import kotlinx.android.synthetic.main.controlmenu_activity.progress_bar

    class ControlMenuActivity : AppCompatActivity() {

        private lateinit var maxScoreTextView: TextView
        private lateinit var statusBarTextView: TextView
        private lateinit var welcomeText: TextView
        private lateinit var sharedPreferences: SharedPreferences
        private var formFilled = false
        private var userPoints = 0
        private var maxScore = 0
        private var isVibrationEnabled = true

        private val vibrationChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == SettingsActivity.ACTION_VIBRATION_CHANGED) {
                    isVibrationEnabled = intent.getBooleanExtra("vibration_enabled", true)
                    // Update the UI or perform any necessary actions based on isVibrationEnabled
                }
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.controlmenu_activity)

            sharedPreferences = getSharedPreferences("quizapp", MODE_PRIVATE)
            formFilled = sharedPreferences.getBoolean("form_filled", false)
            userPoints = sharedPreferences.getInt("user_points", 0)
            maxScore = sharedPreferences.getInt("max_score", 0)

            val firstName = sharedPreferences.getString("firstName", null)
            val lastName = sharedPreferences.getString("lastName", null)

            if (!formFilled || firstName == null || lastName == null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                val donationButton: Button = findViewById(R.id.donationButton)
                val settingsButton: Button = findViewById(R.id.settingsButton)
                val questionButton: Button = findViewById(R.id.questionButton)

                maxScoreTextView = findViewById(R.id.status_bar)
                statusBarTextView = findViewById(R.id.status_text)
                welcomeText = findViewById(R.id.welcomeText)

                displayMaxScore()
                displayStatusBar()
                displayWelcomeMessage()

                donationButton.setOnClickListener {
                    if (isVibrationEnabled) {
                        VibrationUtil.vibrate(this)
                    }
                    val webLink = "https://uniteforchange.com/en/fund/protect-the-environment-fund/" // Replace with your desired web link
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webLink))
                    startActivity(intent)
                }

                settingsButton.setOnClickListener {
                    if (isVibrationEnabled) {
                        VibrationUtil.vibrate(this)
                    }
                    val intent = Intent(this, SettingsActivity::class.java)
                    intent.putExtra("previousActivity", this.javaClass.name)
                    startActivity(intent)

                }

                questionButton.setOnClickListener {
                    if (isVibrationEnabled) {
                        VibrationUtil.vibrate(this)
                    }
                    val intent = Intent(this, QuestionActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        override fun onResume() {
            super.onResume()
            // Fetch the latest vibration status from SharedPreferences
            isVibrationEnabled = sharedPreferences.getBoolean("vibration_enabled", true)
            // Update the UI or perform any necessary actions based on isVibrationEnabled
        }


        override fun onPause() {
            super.onPause()
            unregisterReceiver(vibrationChangeReceiver)
        }

        private fun displayMaxScore() {
            maxScoreTextView.text = "Max Score: $maxScore"
        }

        private fun displayStatusBar() {
            val statusText: String = when {
                maxScore in 0..1999 -> "Νεοεισερχόμενος"
                maxScore in 2000..5999 -> "Αρχάριος"
                maxScore in 6000..9999 -> "Μάστερ"
                maxScore >= 10000 -> "Ακτιβιστής"
                else -> "Ήρωας του Πράσινου"
            }
            statusBarTextView.text = statusText
            displayProgressBar()
        }

        private fun displayProgressBar() {
            val progress: Int = when {
                maxScore in 0..1999 -> ((maxScore.toDouble() / 2000) * 100).toInt()
                maxScore in 2000..5999 -> (((maxScore - 2000).toDouble() / 4000) * 100).toInt()
                maxScore in 6000..9999 -> (((maxScore - 6000).toDouble() / 4000) * 100).toInt()
                maxScore >= 10000 -> 100
                else -> 0
            }
            progress_bar.progress = progress
        }

        private fun displayWelcomeMessage() {
            val firstName = sharedPreferences.getString("firstName", null)?.removeSuffix("s")?.removeSuffix("ς")
            val lastName = sharedPreferences.getString("lastName", null)?.removeSuffix("s")?.removeSuffix("ς")
            welcomeText.text = "Hello, ${firstName.orEmpty()} ${lastName.orEmpty()}"
        }

    }
