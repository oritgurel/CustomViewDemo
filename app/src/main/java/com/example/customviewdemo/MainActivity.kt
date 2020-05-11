package com.example.customviewdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PianoView.IKeyboardListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        keyboard_view.listener = this
    }

    override fun keyOn(key: PianoView.Key) {
        monitor.text = key.name
        monitor.setTextColor(ContextCompat.getColor(this, if (!key.isBlack) R.color.colorAccent else R.color.magenta))
    }

    override fun keyOff(key: PianoView.Key) {
        monitor.text = ""
    }
}
