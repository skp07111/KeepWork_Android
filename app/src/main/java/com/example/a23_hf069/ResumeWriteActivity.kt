package com.example.a23_hf069

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import java.io.IOException

class ResumeWriteActivity : AppCompatActivity() {
    private var IP_ADDRESS = "3.34.190.0" // Replace with your IP address.
    private var userId: String = "" // User ID

    private lateinit var editTextAcademic: EditText
    private lateinit var editTextCareer: EditText
    private lateinit var editTextIntroduction: EditText
    private lateinit var editTextCertificate: EditText
    private lateinit var editTextEducation: EditText
    private lateinit var editTextDesire: EditText
    private var resume_complete: Int = 0
    private lateinit var buttonSubmit1: Button
    private lateinit var buttonSubmit2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resume_write)

        // Get user ID
        userId = intent.getStringExtra("userId") ?: ""

        val textID = findViewById<TextView>(R.id.tvID)
        textID.text = userId

        editTextAcademic = findViewById(R.id.edtAcademic)
        editTextCareer = findViewById(R.id.edtCareer)
        editTextIntroduction = findViewById(R.id.edtIntroduction)
        editTextCertificate = findViewById(R.id.edtCertificate)
        editTextEducation = findViewById(R.id.edtEducation)
        editTextDesire = findViewById(R.id.edtDesire)
        buttonSubmit1 = findViewById(R.id.buttonSubmit1)
        buttonSubmit2 = findViewById(R.id.buttonSubmit2)

        buttonSubmit1.setOnClickListener {
            val personal_id = userId
            val resume_academic = editTextAcademic.text.toString()
            val resume_career = editTextCareer.text.toString()
            val resume_introduction = editTextIntroduction.text.toString()
            val resume_certificate = editTextCertificate.text.toString()
            val resume_learning = editTextEducation.text.toString()
            val resume_desire = editTextDesire.text.toString()
            resume_complete = 0

            sendResumeData(
                personal_id,
                resume_academic,
                resume_career,
                resume_introduction,
                resume_certificate,
                resume_learning,
                resume_desire,
                resume_complete
            )
        }

        buttonSubmit2.setOnClickListener {
            val personal_id = userId
            val resume_academic = editTextAcademic.text.toString()
            val resume_career = editTextCareer.text.toString()
            val resume_introduction = editTextIntroduction.text.toString()
            val resume_certificate = editTextCertificate.text.toString()
            val resume_learning = editTextEducation.text.toString()
            val resume_desire = editTextDesire.text.toString()
            resume_complete = 1

            sendResumeData(
                personal_id,
                resume_academic,
                resume_career,
                resume_introduction,
                resume_certificate,
                resume_learning,
                resume_desire,
                resume_complete
            )
        }
    }

    private fun sendResumeData(
        personal_id: String,
        resume_academic: String,
        resume_career: String,
        resume_introduction: String,
        resume_certificate: String,
        resume_learning: String,
        resume_desire: String,
        resume_complete: Int
    ) {
        val url = "http://$IP_ADDRESS/android_resume_php.php" // URL of the hosting server with PHP script

        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("personal_id", personal_id) // ID
            .add("resume_academic", resume_academic) // Education
            .add("resume_career", resume_career) // Career
            .add("resume_introduction", resume_introduction) // Introduction
            .add("resume_certificate", resume_certificate) // Certification
            .add("resume_learning", resume_learning) // Education history
            .add("resume_desire", resume_desire) // Desired job position
            .add("resume_complete", resume_complete)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle request failure
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle request success
                val responseData = response.body?.string()
            }
        })
    }
}
