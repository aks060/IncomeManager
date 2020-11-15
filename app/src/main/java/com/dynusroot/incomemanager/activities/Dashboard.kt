package com.dynusroot.incomemanager.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.adapters.AccountsAdapter
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.database.models.accounts
import com.dynusroot.incomemanager.viewModels.DashboardViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.jar.Manifest

class Dashboard : AppCompatActivity() {
    private lateinit var adapter: AccountsAdapter
    private lateinit var accounts:ArrayList<accounts>
    private lateinit var viewModel: DashboardViewModel
    private lateinit var gridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val db=incomemanager_db.get(application).dbDao
        viewModel= DashboardViewModel(db, application)
        accounts= ArrayList()
        accounts= viewModel.accountlist.value!!
        gridView=findViewById<GridView>(R.id.accounts)

        viewModel.accountlist.observe(this, Observer {
            accounts=it
            adapter= AccountsAdapter(this, accounts)
            gridView.adapter=adapter
        })
//        adapter= AccountsAdapter(this, accounts)
//        var gridView=findViewById<GridView>(R.id.accounts)
//        gridView.adapter=adapter


        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            var intent= Intent(this, AccountPage::class.java)
            var bundle=Bundle()
            var name=view.findViewById<TextView>(R.id.account_name).text.toString()
            var accountid=view.findViewById<TextView>(R.id.accountid).text.toString()
            bundle.putString("account", name)
            bundle.putString("accountid", accountid)
            Log.i("Dashboard", accountid)
            intent.putExtras(bundle)
            startActivity(intent)
        }
//        gridView.setOnClickListener {
//            var name=it.findViewById<TextView>(R.id.account_name).text.toString()
//            bundle.putString("account", name)
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }


        var addnew=findViewById<FloatingActionButton>(R.id.addnewaccount)
        addnew.setOnClickListener {
            startActivity(Intent(this, AddAccount::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
        viewModel.accountlist.observe(this, Observer {
            accounts=it
            adapter= AccountsAdapter(this, accounts)
            gridView.adapter=adapter
        })
    }
}

