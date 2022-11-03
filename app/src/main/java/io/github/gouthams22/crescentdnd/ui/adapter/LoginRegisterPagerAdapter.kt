package io.github.gouthams22.crescentdnd.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.gouthams22.crescentdnd.ui.fragment.LoginFragment
import io.github.gouthams22.crescentdnd.ui.fragment.RegisterFragment

class LoginRegisterPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment.newInstance()
            1 -> RegisterFragment.newInstance()
            else -> Fragment()
        }
    }
}