package com.ta.pcpoc.phone

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return CallLogFRagment()
            1 -> return SMSLogFragment()
        }
        return CallLogFRagment()
    }

    override fun getCount(): Int {
        return 2
    }
}