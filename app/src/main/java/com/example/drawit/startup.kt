package com.example.drawit

import android.content.Intent
import android.os.Bundle

import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.HandlerCompat.postDelayed

import java.util.*


class startup : AppCompatActivity() {
    // Create a new event for the activity.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout for the content view.
        setContentView(R.layout.activity_startup)

        Handler().postDelayed({
            val intent = Intent(this@startup,MainActivity::class.java)
            startActivity(intent)
            finish()
        },3000)

    }
}

