package com.ytowka.unotes.screens.edit.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ytowka.unotes.R
import com.ytowka.unotes.databinding.FragmentOverviewBinding


class OverviewFragment : Fragment() {
    lateinit var binding: FragmentOverviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOverviewBinding.inflate(inflater)
        return binding.root
    }
}