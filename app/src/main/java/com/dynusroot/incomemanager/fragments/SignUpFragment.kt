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
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.objects.user
import com.dynusroot.incomemanager.viewModels.Splash_ScreenViewModel

class SignUpFragment : Fragment() {

    private lateinit var viewModel: Splash_ScreenViewModel
    private lateinit var views: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db=incomemanager_db.get(requireActivity().application).dbDao
        viewModel=Splash_ScreenViewModel(db, requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        views=inflater.inflate(R.layout.fragment_sign_up, container, false)

        //Signup
        signup()

        viewModel.toastmssg.observe(requireActivity(), Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })

        views.findViewById<TextView>(R.id.login_text).setOnClickListener {
            views.findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
        return views.rootView
    }

    private fun signup()
    {
        views.findViewById<Button>(R.id.signup).setOnClickListener {
            var fullname = views.findViewById<EditText>(R.id.name).text.toString()
            var username = views.findViewById<EditText>(R.id.username).text.toString()
            var password = views.findViewById<EditText>(R.id.password).text.toString()
            var repass = views.findViewById<EditText>(R.id.repassword).text.toString()
            if (fullname == "" || username == "" || password == "" || repass == "") {
                Toast.makeText(requireActivity(), "Please Enter all the detail", Toast.LENGTH_LONG)
                    .show()
            } else {
                if (password != repass) {
                    Toast.makeText(
                        requireContext(),
                        "Password and Re-Password are not same",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    viewModel.signup(fullname, username, password)
                    viewModel.ret.observe(requireActivity(), Observer {
                        if(it==1)
                        {
                            user.fullname = fullname
                            user.username = username
                            startActivity(Intent(requireContext(), Dashboard::class.java))
                        }
                    })
                }
            }
        }
    }

}