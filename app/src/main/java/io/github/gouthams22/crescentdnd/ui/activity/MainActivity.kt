package io.github.gouthams22.crescentdnd.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.button.MaterialButton
import io.github.gouthams22.crescentdnd.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        findViewById<MaterialButton>(R.id.btn_get_started).setOnClickListener {
            startActivity(Intent(this, LoginRegisterActivity::class.java))
        }
    }
}