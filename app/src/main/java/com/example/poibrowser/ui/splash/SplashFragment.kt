package com.example.poibrowser.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.example.poibrowser.R
import com.example.poibrowser.utils.delay

class SplashFragment : Fragment() {
    private val SPLASH_DISPLAY_LENGTH : Long = 2000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchMaps()
    }

    private fun launchMaps() {
        delay(SPLASH_DISPLAY_LENGTH) {
            navigateToMaps()
        }
    }

    private fun navigateToMaps() {
        val nc = NavHostFragment.findNavController(this)
        nc.navigate(SplashFragmentDirections.actionSplashFragmentToMapsFragment())
    }
}