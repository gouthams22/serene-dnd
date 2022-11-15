package io.github.gouthams22.crescentdnd.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.github.gouthams22.crescentdnd.R
import io.github.gouthams22.crescentdnd.ui.adapter.LoginRegisterPagerAdapter

class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
        window.statusBarColor = this.resources.getColor(R.color.deep_orange_700, theme)

        //Tab Layout and its ViewPager
        val tabLayout = findViewById<TabLayout>(R.id.login_register_tab)
        val viewPager = findViewById<ViewPager2>(R.id.login_register_viewpager)
        viewPager.adapter = LoginRegisterPagerAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Login"
                1 -> "Register"
                else -> "Error"
            }
        }.attach()
    }

    /**
     * [haveAccount] is used to change tab from register to login if "Hava an account?" is clicked
     */
    fun haveAccount() {
        val tabLayout = findViewById<TabLayout>(R.id.login_register_tab)
        tabLayout.selectTab(tabLayout.getTabAt(0))
    }
}