package io.github.gouthams22.serenednd.ui.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.serenednd.BuildConfig
import io.github.gouthams22.serenednd.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        findViewById<MaterialTextView>(R.id.text_version_name).text = BuildConfig.VERSION_NAME

        findViewById<ImageView>(R.id.logo_github).setOnClickListener {
            //TODO: Implement Github
        }

        findViewById<ImageView>(R.id.logo_linkedin).setOnClickListener {
            //TODO: Implement LinkedIn
        }

        findViewById<ImageView>(R.id.logo_mail).setOnClickListener {
            //TODO: Implement mail
        }

    }
}