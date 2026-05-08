package com.example.pancreatic

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load

class PlotDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plot_detail)

        val title = findViewById<TextView>(R.id.title)
        val image = findViewById<ImageView>(R.id.image)

        val filename = intent.getStringExtra("filename") ?: "Plot"
        val url = intent.getStringExtra("url") ?: ""

        title.text = filename
        if (url.isNotBlank()) {
            image.load(url) { crossfade(true) }
        }
    }
}

