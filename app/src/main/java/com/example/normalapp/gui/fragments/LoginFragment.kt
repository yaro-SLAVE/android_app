package com.example.normalapp.gui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.normalapp.databinding.FragmentLoginBinding
import com.example.normalapp.gui.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment: Fragment() {
//    private lateinit var loginEdit: EditText
//    private lateinit var passwordEdit: EditText
//    private lateinit var loginButton: Button

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = loginViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        loginEdit = binding.loginEditText
//        passwordEdit = binding.passwordEditText
//        loginButton = binding.logInButton

//        loginButton.setOnClickListener {
    //            CoroutineScope(Dispatchers.IO).launch {
//                print(loginEdit.text.toString() + "____________________")
//                loginViewModel.login(loginEdit.text.toString(), passwordEdit.text.toString())
//            }
//            Toast.makeText(context, "Ваше сообщение", Toast.LENGTH_SHORT).show()
//        }
    }
}