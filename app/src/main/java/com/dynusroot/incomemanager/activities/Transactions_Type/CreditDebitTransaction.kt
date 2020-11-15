package com.dynusroot.incomemanager.activities.Transactions_Type

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.database.models.subaccounts
import com.dynusroot.incomemanager.viewModels.AddTransactionViewModel
import java.util.*
import kotlin.collections.ArrayList

class CreditDebitTransaction : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private var subaccountid=-1L
    private lateinit var viewModel:AddTransactionViewModel
    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private lateinit var date:TextView
    private var orderByDate:String=""
    private lateinit var transactiontype:String
    private var subaccList: ArrayList<subaccounts> = ArrayList()
    private lateinit var spinner:Spinner
    private var toBeTransferAcc: Long? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("CreditDebit", "OnCreate")
        setContentView(R.layout.activity_credit_debit_transaction)
        var bundle=intent.extras
        subaccountid= bundle!!.getString("subaccountid").toString().toLong()
        transactiontype=bundle!!.getString("transactiontype").toString()
        val db=incomemanager_db.get(application).dbDao
        viewModel= AddTransactionViewModel(db, application, subaccountid)

        viewModel.getSubAccountList()
        viewModel.accountList.observe(this, androidx.lifecycle.Observer {
            subaccList=it
        })


        var title=findViewById<TextView>(R.id.title)
        if(transactiontype=="C")
        {
            title.text="Add Transaction (Credit)"
        }
        else if(transactiontype=="D")
            title.text="Add Transaction (Debit)"
        else if(transactiontype=="T") {
            title.text = "Add Transaction (Transfer)"
            var selectaccountwala=findViewById<LinearLayout>(R.id.select_accountwala)
            selectaccountwala.visibility=View.VISIBLE
        }


        date=findViewById<EditText>(R.id.date)
        date.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                    DatePickerDialog(this, this, year, month,day)
            datePickerDialog.show()
        }

        var options:ArrayList<String> = ArrayList()
        for (i in subaccList){
            options.add(i.name)
        }



        if(transactiontype=="T") {
            spinner = findViewById(R.id.selectaccount)
            spinner.adapter=ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    toBeTransferAcc=subaccList.get(position).id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(this@CreditDebitTransaction, "Please Select Account to transfer", Toast.LENGTH_LONG).show()
                    toBeTransferAcc=null
                }

            }
        }

        var submit=findViewById<Button>(R.id.add)
        submit.setOnClickListener {
            var amount = findViewById<EditText>(R.id.amount).text.toString().toDouble()
            var desc = findViewById<EditText>(R.id.description).text.toString()
//            var date = findViewById<EditText>(R.id.date).text.toString()

            var datetext=date.text.toString()
            if (amount > 0 && desc != "" && datetext!="") {
                if(transactiontype=="C") {
                    viewModel.creditmoney(amount = amount, desc, datetext, orderByDate)
                    Toast.makeText(this, "Amount Credited", Toast.LENGTH_LONG).show()
                }
                else if(transactiontype=="D"){
                    viewModel.debitmoney(amount, desc, datetext, orderByDate)
                    Toast.makeText(this, "Amount Debited", Toast.LENGTH_LONG).show()
                }
                else
                    if(transactiontype=="T")
                    {
                        if(toBeTransferAcc!=null) {
                            viewModel.transfermoney(amount, desc, datetext, toBeTransferAcc!!, orderByDate)
                        }
                        else
                        {
                            Toast.makeText(this, "Please Select Account", Toast.LENGTH_LONG).show()
                        }
                    }

            } else {
                Toast.makeText(this, "Amount should be >0 All fields are required", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("CreditDebit", "OnResume")
        viewModel.refresh()
    }

    override fun onPause() {
        super.onPause()
        Log.e("CreditDebit", "OnPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("CreditDebit", "OnDestroy")
    }

    @Override
    override fun onDateSet(view: DatePicker?, year1: Int, month1: Int, dayOfMonth: Int) {
        day = dayOfMonth
        year = year1
        month = month1
        date.setText(day.toString()+"/"+(month+1)+"/"+year)
        orderByDate=year.toString()+"/"+(month+1)+"/"+day
    }
}