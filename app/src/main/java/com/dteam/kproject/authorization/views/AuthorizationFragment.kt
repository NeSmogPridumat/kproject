package com.dteam.kproject.authorization.views

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.dteam.kproject.R
import com.dteam.kproject.authorization.viewModel.AuthorizationViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthorizationFragment : Fragment() {

    private lateinit var phoneEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var sendButton: Button
    private lateinit var registrationTextView: TextView
    private val viewModel: AuthorizationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findNavController().popBackStack(R.id.calendarFragment, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_authorization, container, false)
        phoneEditText = view.findViewById(R.id.phone_edit_text)
        phoneEditText.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))

        passwordEditText = view.findViewById(R.id.password_edit_text)
        registrationTextView = view.findViewById(R.id.registration_text_view)
        registrationTextView.setOnClickListener {
            findNavController().navigate(R.id.action_setPhoneFragment_to_registrationFragment)
        }
        sendButton = view.findViewById(R.id.send_button)
        sendButton.setOnClickListener {
            checkPhone()
        }

        viewModel.getCheckLD().observe(this as LifecycleOwner, {
            it.getContentIfNotHandler()?.let {
                findNavController().navigate(R.id.action_setPhoneFragment_to_calendarFragment)
            }
        })

        viewModel.getErrorLiveData().observe(this as LifecycleOwner, {
            it.getContentIfNotHandler()?.let { errorText ->
                Toast.makeText(view.context, errorText, Toast.LENGTH_LONG).show()
            }
        })
        return view
    }

    private fun checkPhone() {
        val phone = "+7" + phoneEditText.text
        val password = passwordEditText.text.toString()
        viewModel.authorization(phone, password)
    }
}