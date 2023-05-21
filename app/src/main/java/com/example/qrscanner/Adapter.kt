package com.example.qrscanner

import androidx.camera.core.ExperimentalGetImage
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.io.Serializable

@ExperimentalGetImage class Adapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment),
    Serializable {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) ScannerFragment()
        else GeneratorFragment()
    }

}