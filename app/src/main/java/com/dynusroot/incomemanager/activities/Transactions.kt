package com.dynusroot.incomemanager.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.activities.Transactions_Type.CreditDebitTransaction
import com.dynusroot.incomemanager.adapters.SubAccountTransactionAdapter
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.database.models.transactions
import com.dynusroot.incomemanager.viewModels.TransactionsViewModel

class Transactions : AppCompatActivity(), SubAccountTransactionAdapter.popupOption {
    private lateinit var viewModel:TransactionsViewModel
    private var accountid=""
    private var subaccountid=""
    private lateinit var transactionlists: RecyclerView
    private var accountname=""
    private var subaccountname=""
    private lateinit var adapter:SubAccountTransactionAdapter
    private var tracker: SelectionTracker<Long>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("Transactions", "OnCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)
        var bundle=intent.extras
        accountid= bundle?.getString("accountid").toString()
        accountname=bundle?.getString("accountname").toString()
        subaccountname=bundle?.getString("subaccountname").toString()
        subaccountid=bundle?.getString("subaccountid").toString()


        val db=incomemanager_db.get(application).dbDao
        Log.i("TransactionAccID", "AccID: "+accountid)
        Log.i("TransactionSubID", "SubID: "+subaccountid)
        viewModel=TransactionsViewModel(db, application, accountid = accountid.toLong(), subaccountid.toLong())

        transactionlists=findViewById<RecyclerView>(R.id.recyclerView)
        viewModel.transactions.observe(this, Observer {
            adapter= SubAccountTransactionAdapter(it, this, this)
            initRecyclerView()
            viewModel.updateBalance()
        })

        var delete=findViewById<ImageButton>(R.id.deletesubaccount)
        delete.setOnClickListener {
            var alert= AlertDialog.Builder(this)
            alert.setTitle("Are you sure?")
            alert.setMessage("Do you want to delete this Subaccount?")
            alert.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                viewModel.deletesubaccount()
                Log.e("Transaction", "Clicked")
                onBackPressed()
            }
            alert.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int -> }
            alert.show()
        }

        var credit=findViewById<ImageButton>(R.id.creditbutton)
        credit.setOnClickListener {
            var intent=Intent(this, CreditDebitTransaction::class.java)
            var bundle=Bundle()
            bundle.putString("subaccountid", subaccountid)
            bundle.putString("transactiontype", "C")
            intent.putExtras(bundle)
            startActivity(intent)
        }

        var debit=findViewById<ImageButton>(R.id.debitbutton)
        debit.setOnClickListener {
            var intent=Intent(this, CreditDebitTransaction::class.java)
            var bundle=Bundle()
            bundle.putString("subaccountid", subaccountid)
            bundle.putString("transactiontype", "D")
            intent.putExtras(bundle)
            startActivity(intent)
        }

        var transfer=findViewById<ImageView>(R.id.transfer)
        transfer.setOnClickListener {
            var intent=Intent(this, CreditDebitTransaction::class.java)
            var bundle=Bundle()
            bundle.putString("subaccountid", subaccountid)
            bundle.putString("transactiontype", "T")
            intent.putExtras(bundle)
            startActivity(intent)
        }

        var title=findViewById<TextView>(R.id.accname)
        title.text=accountname+" >> "+subaccountname

        var total=findViewById<TextView>(R.id.total)
        total.text="Rs 0"

        viewModel.total.observe(this, Observer {
            total.text="Rs "+it.toString()
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
        Log.e("Transactions", "OnResume")
    }

    fun initRecyclerView(){
        transactionlists.layoutManager = LinearLayoutManager(this)
        transactionlists.adapter=adapter

//        tracker = SelectionTracker.Builder<Long>(
//            "selection-1",
//            transactionlists,
//            StableIdKeyProvider(transactionlists),
//            MyLookup(my_rv),
//            StorageStrategy.createLongStorage()
//        ).withSelectionPredicate(
//            SelectionPredicates.createSelectAnything()
//        ).build()

//        tracker=SelectionTracker.Builder<Long>()
    }

    override fun delete(position: Int) {
        viewModel.deleteTransaction(viewModel.transactions.value?.get(position)!!.id, position)
        adapter= SubAccountTransactionAdapter(viewModel.transactions.value as ArrayList<transactions>, this, this)
        initRecyclerView()
        viewModel.updateBalance()
    }

}