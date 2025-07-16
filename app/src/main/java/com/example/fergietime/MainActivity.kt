package com.example.fergietime

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    private var isDarkTheme = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layout = findViewById<ConstraintLayout>(R.id.rootLayout)
        val textView = findViewById<TextView>(R.id.textView)
        val button = findViewById<Button>(R.id.buttonSwitchTheme)

        button.setOnClickListener {
            isDarkTheme = !isDarkTheme

            if (isDarkTheme) {
                textView.text = "ダークテーマが有効"
                layout.setBackgroundColor(Color.BLACK)
                textView.setTextColor(Color.WHITE)
                button.setBackgroundColor(Color.DKGRAY)
                button.setTextColor(Color.WHITE)
            } else {
                textView.text = "ライトテーマが有効"
                layout.setBackgroundColor(Color.WHITE)
                textView.setTextColor(Color.BLACK)
                button.setBackgroundColor(Color.LTGRAY)
                button.setTextColor(Color.BLACK)
            }
        }
    }
}
