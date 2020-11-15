package com.dynusroot.incomemanager.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.lifecycle.Observer
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.adapters.AccountsAdapter
import com.dynusroot.incomemanager.adapters.SubAccountsAdapter
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.database.models.accounts
import com.dynusroot.incomemanager.database.models.subaccounts
import com.dynusroot.incomemanager.viewModels.AccountPageViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AccountPage : AppCompatActivity() {
    private var accountid=""
    private lateinit var adapter: SubAccountsAdapter
    private lateinit var subaccounts:ArrayList<subaccounts>
    private lateinit var viewModel:AccountPageViewModel
    private lateinit var gridView: GridView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_page)

        var intent=getIntent()
        var bundle=intent.extras
        accountid= bundle?.getString("accountid").toString()
        findViewById<TextView>(R.id.accname).text=bundle?.getString("account")

        var db=incomemanager_db.get(application).dbDao
        viewModel= AccountPageViewModel(db, application, accountid = accountid.toLong())
        gridView=findViewById<GridView>(R.id.accounts)

        //Toast Alert
        viewModel.toastmssg.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        //delete button
        var delete=findViewById<Button>(R.id.delete)
        delete.setOnClickListener {
            var alert=AlertDialog.Builder(this)
            alert.setTitle("Are you sure?")
            alert.setMessage("Do you want to delete this account?")
            alert.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                viewModel.deleteaccount(bundle?.getString("accountid").toString())
                startActivity(Intent(this, Dashboard::class.java))
            }
            alert.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int -> }
            alert.show()
        }

        //Add button
        var add=findViewById<FloatingActionButton>(R.id.addsubaccount)
        add.setOnClickListener {
            var intent=Intent(this, AddSubAccount::class.java)
            if (bundle != null) {
                intent.putExtras(bundle)
            }
            startActivity(intent)
        }


        viewModel.subaccountlist.observe(this, Observer {
            subaccounts=it
            adapter= SubAccountsAdapter(this, subaccounts)
            gridView.adapter=adapter
        })

        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            var bundle=Bundle()
            var intent=Intent(this, Transactions::class.java)
            bundle.putString("accountid", accountid)
            bundle.putString("accountname", bundle?.getString("account"))
            bundle.putString("subaccountid", adapter.data.get(id.toInt()).id.toString())
            bundle.putString("subaccountname", view.findViewById<TextView>(R.id.account_name).text.toString())
            intent.putExtras(bundle)

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("AccountPage", "Resume")
        viewModel.refresh()
        viewModel.subaccountlist.observe(this, Observer {
            subaccounts=it
            adapter= SubAccountsAdapter(this, subaccounts)
            gridView.adapter=adapter
        })
    }
}