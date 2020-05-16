package com.example.customviewdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PianoView.IKeyboardListener {

    private var toggle: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        keyboard_view.listener = this

        toggle_theme_btn.setOnClickListener {
            keyboard_view.apply {
                whiteKeysColor = ContextCompat.getColor(this@MainActivity, if (toggle) R.color.colorAccent else R.color.white)
                blackKeysColor = ContextCompat.getColor(this@MainActivity, if (toggle) R.color.yellow else R.color.black)
                toggle = !toggle
            }

        }
    }

    override fun keyOn(key: PianoView.Key) {
        monitor.text = key.name
        monitor.setTextColor(ContextCompat.getColor(this, if (!key.isBlack) R.color.colorAccent else R.color.magenta))
    }

    override fun keyOff(key: PianoView.Key) {
        monitor.text = ""
    }
}
