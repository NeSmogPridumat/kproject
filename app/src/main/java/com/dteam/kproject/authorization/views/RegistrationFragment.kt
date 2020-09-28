package com.dteam.kproject.authorization.views

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.dteam.kproject.R
import com.dteam.kproject.authorization.viewModel.AuthorizationViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val viewModel:AuthorizationViewModel  by activityViewModels()
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var sendButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)
        passwordEditText = view.findViewById(R.id.password_edit_text)

        phoneEditText = view.findViewById(R.id.phone_edit_text)
        phoneEditText.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))

        nameEditText = view.findViewById(R.id.name_edit_text)
        sendButton = view.findViewById(R.id.send_button)
        sendButton.setOnClickListener {
            registration()
        }

        viewModel.getCheckLD().observe(this as LifecycleOwner, {
            it.getContentIfNotHandler()?.let {
                findNavController().navigate(R.id.action_registrationFragment_to_calendarFragment)
            }
        })

        viewModel.getErrorLiveData().observe(this as LifecycleOwner, {
            it.getContentIfNotHandler()?.let { errorText ->
                Toast.makeText(view.context, errorText, Toast.LENGTH_LONG).show()
            }
        })
        return view
    }

    private fun registration() {
        val phone = "+7" + phoneEditText.text
        val name = nameEditText.text.toString()
        val password = passwordEditText.text.toString()

        if(phone.trim().isNotEmpty() && name.trim().isNotEmpty() && password.trim().isNotEmpty())
            viewModel.registration(phone.trim(), name.trim(), password.trim())
        else {
            val errorString = "Заполните все поля"
            nameEditText.error = errorString
            phoneEditText.error = errorString
        }
    }
}