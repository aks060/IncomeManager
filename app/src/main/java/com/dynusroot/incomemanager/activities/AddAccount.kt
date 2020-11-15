package com.dynusroot.incomemanager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.lifecycle.Observer
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.viewModels.AddAccountViewModel

class AddAccount : AppCompatActivity() {
    private lateinit var viewModel: AddAccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_account)
        var db=incomemanager_db.get(application).dbDao
        viewModel=AddAccountViewModel(db, application)

        findViewById<Button>(R.id.addnewaccount).setOnClickListener {
            var accname = findViewById<EditText>(R.id.accname).text.toString()
            var accdesc = findViewById<MultiAutoCompleteTextView>(R.id.accdesc).text.toString()
            if(accname=="")
            {
                Toast.makeText(this, "Name Cannot be empty", Toast.LENGTH_LONG).show()
            }
            else
            viewModel.addaccount(accname, accdesc)
        }

        viewModel.toastmssg.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            if(viewModel.issuccess==1)
            {
                onBackPressed()
            }
        })
    }
}