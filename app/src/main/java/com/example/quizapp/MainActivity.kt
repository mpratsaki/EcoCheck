package com.example.quizapp

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var input: AppCompatEditText
    private lateinit var input2: AppCompatEditText
    private lateinit var input3: AppCompatEditText
    private lateinit var input4: AppCompatEditText
    private val choices = arrayOf("Λύκειο", "Πανεπιστήμιο", "Μεταπτυχιακό", "Διδακτορικό")
    private var formFilled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val sharedPreferences = getSharedPreferences("quizapp", Context.MODE_PRIVATE)
        formFilled = sharedPreferences.getBoolean("form_filled", false)

        input = findViewById(R.id.input) // Initialize input variable

        if (formFilled) {
            val intent = Intent(this, ControlMenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        button.setOnClickListener {
            if (input.text.toString().isEmpty()) {
                Toast.makeText(this, "Πληκτρολογήστε το όνομα σας", Toast.LENGTH_SHORT).show()
            } else if (input2.text.toString().isEmpty()) {
                Toast.makeText(this, "Πληκτρολογήστε το επίθετο σας", Toast.LENGTH_SHORT).show()
            } else if (input3.text.toString().isEmpty()) {
                Toast.makeText(this, "Επιλέξτε τον ακαδημαικό σας τίτλο", Toast.LENGTH_SHORT).show()
            } else if (input4.text.toString().isEmpty()) {
                Toast.makeText(this, "Επιλέξτε ημερομηνία γέννησης", Toast.LENGTH_SHORT).show()
            } else {
                // Store the form filled status and input values in SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putBoolean("form_filled", true)
                editor.putString("firstName", input.text.toString())
                editor.putString("lastName", input2.text.toString())
                editor.apply()

                val intent = Intent(this, ControlMenuActivity::class.java)
                startActivity(intent)
                finish()
            }
        }



        input2 = findViewById(R.id.input2)
        input2.setOnClickListener {
            showChoicesDialog()
        }

        input3 = findViewById(R.id.input3)
        input3.setOnClickListener {
            showChoicesDialog()
        }

        input4 = findViewById(R.id.input4)
        input4.setOnClickListener {
            showDatePicker()
        }

        // Inside MainActivity
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("previous_activity", "MainActivity")
            startActivity(intent)
        }
    }

    private fun showChoicesDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")

        val spinner = AppCompatSpinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, choices)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        builder.setView(spinner)

        builder.setPositiveButton("OK") { dialog, which ->
            val selectedPosition = spinner.selectedItemPosition
            val selectedChoice = choices[selectedPosition]
            input3.setText(selectedChoice)
        }

        builder.setNegativeButton("Cancel", null)

        val dialog = builder.create()
        dialog.show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDate = "${dayOfMonth}/${month + 1}/${year}"
            input4.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}
