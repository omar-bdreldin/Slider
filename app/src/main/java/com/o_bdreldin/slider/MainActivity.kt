package com.o_bdreldin.slider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        slider.onSlideListener = object : Slider.OnSlideListener {
            override fun onSlide(progress: Float) {
                val percentage = progress * 100f
                progress_text_view.text = "$percentage %"
            }
        }
    }
}
