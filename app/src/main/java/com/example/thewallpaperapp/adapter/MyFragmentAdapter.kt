package com.example.thewallpaperapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.thewallpaperapp.fragments.CategoryFragment
import com.example.thewallpaperapp.fragments.RecentFragment
import com.example.thewallpaperapp.fragments.TrendingFragment

class MyFragmentAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CategoryFragment.getInstance()!!
            1 -> TrendingFragment.getInstance()!!
            2 -> RecentFragment.getInstance()!!
            else -> Fragment()
        }

    }


    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Collections"
            1 -> return "Trending"
            2 -> return "Recent"
        }
        return ""
    }
}