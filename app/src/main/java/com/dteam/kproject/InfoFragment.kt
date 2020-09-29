package com.dteam.kproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class InfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setTitle()
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    private fun setTitle() {
        try {
            requireActivity().title = resources.getString(R.string.info)
        } catch (t: Throwable){
            t.printStackTrace()
        }
    }
}