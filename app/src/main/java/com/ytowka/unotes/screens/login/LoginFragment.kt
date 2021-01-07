package com.ytowka.unotes.screens.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ytowka.unotes.databinding.FragmentLoginBinding
import com.ytowka.unotes.screens.MainViewModel
import com.ytowka.unotes.screens.MainViewModelFactory


class LoginFragment : Fragment() {
    companion object {
        const val RC_SIGN_IN = 1
    }

    lateinit var viewModel: MainViewModel
    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity(), MainViewModelFactory(requireActivity().application))
            .get(MainViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("debug","current destination: авторизация")

        viewModel.authentication.firebaseUserLiveData.observe(viewLifecycleOwner){
            if(it != null){
                val action = LoginFragmentDirections.loginAction()
                findNavController().navigate(action)
            }

        }
        binding.loginFragmentLogBtn.setOnClickListener {
            val signInIntent = viewModel.authentication.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            viewModel.authentication.postIntentResult(data)
        }
    }

}