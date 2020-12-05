package com.ytowka.unotes.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ytowka.unotes.R
import com.ytowka.unotes.screens.login.MainViewModel
import com.ytowka.unotes.screens.login.MainViewModelFactory

class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        ViewModelProvider(this,MainViewModelFactory(requireActivity().application)).get(MainViewModel::class.java).firebaseUserLiveData.observe(viewLifecycleOwner){
            Log.i("debug","data have got")
            val action = if(it != null) SplashFragmentDirections.actionIfLogined() else SplashFragmentDirections.actionIfNotLogined()
            findNavController().navigate(action)
        }
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

}