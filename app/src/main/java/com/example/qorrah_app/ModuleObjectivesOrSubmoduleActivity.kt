package com.example.qorrah_app

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.View

class ModuleObjectivesOrSubmoduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_module_objectives_or_submodule)

        //  Go back to Course Outline
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)

        // Go Forward to Actual Sequential Learning Content
        val objectiveCard001 = findViewById<View>(R.id.objectiveCard001)

        objectiveCard001.setOnClickListener {
            val intent = Intent(
                this,
                ActualSequentialLearningContentActivity::class.java
            )
            startActivity(intent)
        }

        buttonBack.setOnClickListener {
            finish()
        }
    }
}