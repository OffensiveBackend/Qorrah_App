package com.example.qorrah_app

import android.os.Bundle
import android.widget.ImageButton
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class CourseOutlineAndModulesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity__courseoutline_and_modules)

        // Back to Home
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)

        buttonBack.setOnClickListener {
            finish()
        }

        // Open Module 01
        val moduleCard001 = findViewById<View>(R.id.moduleCard001)

        moduleCard001.setOnClickListener {
            val intent = Intent(
                this,
                ModuleObjectivesOrSubmoduleActivity::class.java
            )
            startActivity(intent)
        }
    }
}