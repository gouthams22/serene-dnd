package io.github.gouthams22.serenednd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val versionNameTextView: MaterialTextView = findViewById(R.id.text_version_name)
        versionNameTextView.text = BuildConfig.VERSION_NAME
    }
}