package com.dynusroot.incomemanager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.viewModels.AddSubAccountViewModel
import android.util.Log

class AddSubAccount : AppCompatActivity() {
    private lateinit var viewModel: AddSubAccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sub_account)

        var intent=intent
        var bundle=intent.extras
        var parentaccount= bundle?.getString("accountid")!!.toLong()
        var accountname=bundle.getString("account").toString()

        val db=incomemanager_db.get(application).dbDao
        viewModel= AddSubAccountViewModel(db, application)

        viewModel.toastmssg.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        var submit=findViewById<Button>(R.id.addnewsubaccount)
        submit.setOnClickListener {
            var subname=findViewById<EditText>(R.id.accsubname).text.toString()
            var desc=findViewById<MultiAutoCompleteTextView>(R.id.accsubdesc).text.toString()
            var bal=findViewById<EditText>(R.id.balance).text.toString()
            if(subname=="" || bal=="")
            {
                Log.i("SubAccount", subname)
                Log.i("SubAccount", bal)
                Toast.makeText(this, "Field Name and Balance are compulsory", Toast.LENGTH_LONG).show()
            }
            else
            {
                viewModel.addsubaccount(subname, desc, bal = bal.toDouble(), parent = parentaccount)
            }
        }

    }
}