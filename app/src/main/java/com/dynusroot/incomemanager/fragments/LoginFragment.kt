package com.dynusroot.incomemanager.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.activities.Dashboard
import com.dynusroot.incomemanager.database.db_dao
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.objects.user
import com.dynusroot.incomemanager.viewModels.LoginFragmentViewModel

class LoginFragment : Fragment() {

    private lateinit var views:View
    private lateinit var viewModel:LoginFragmentViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db=incomemanager_db.get(requireActivity().application).dbDao
        viewModel= LoginFragmentViewModel(db, requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        views=inflater.inflate(R.layout.fragment_login, container, false)

        views.findViewById<TextView>(R.id.signup_text).setOnClickListener {
            views.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        login()
        return views.rootView
    }

    fun login()
    {
        views.findViewById<Button>(R.id.login).setOnClickListener {
            var username = views.findViewById<EditText>(R.id.username).text.toString()
            var password = views.findViewById<EditText>(R.id.password).text.toString()
            if (username == "" || password == "") {
                Toast.makeText(requireActivity(), "Please Enter all the detail", Toast.LENGTH_LONG)
                        .show()
            }
            else {
                viewModel.login(username, password)
                viewModel.login.observe(requireActivity(), Observer {
                    if(it==1)
                    {
                        startActivity(Intent(requireContext(), Dashboard::class.java))
                    }
                    else {
                        Toast.makeText(requireContext(), "Error in login", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }
}