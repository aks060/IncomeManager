package com.dynusroot.incomemanager.activities.Transactions_Type

import android.app.DatePickerDialog
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.database.models.subaccounts
import com.dynusroot.incomemanager.viewModels.AddTransactionViewModel
import java.util.*
import kotlin.collections.ArrayList
import android.widget.AdapterView

import android.widget.AdapterView.OnItemSelectedListener




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
    private lateinit var scheduled: SwitchCompat
    private var scheduleType:String? = null
    private var scheduleTiming:String? = "D"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("CreditDebit", "OnCreate")
        setContentView(R.layout.activity_credit_debit_transaction)
        var datefield = findViewById<LinearLayout>(R.id.date_layout)
        var select_schedule = findViewById<LinearLayout>(R.id.select_schedule)
        var schedule_month = findViewById<LinearLayout>(R.id.schedule_monthly)
        var repeat_option = findViewById<Spinner>(R.id.schedule)
        var weekly_schedule = findViewById<LinearLayout>(R.id.weekly_schedule)
        var week_option = findViewById<Spinner>(R.id.week_option)
        var options = ArrayList<String>()

        var bundle = intent.extras
        subaccountid = bundle!!.getString("subaccountid").toString().toLong()
        transactiontype = bundle!!.getString("transactiontype").toString()
        val db = incomemanager_db.get(application).dbDao
        viewModel = AddTransactionViewModel(db, application, subaccountid, this)
        var Intervaloptions = ArrayList<String>()
        Intervaloptions.addAll(arrayOf("Daily", "Weekly", "Monthly", "Yearly"))
        Log.e("Options", Intervaloptions.toString())

        var weekdays=arrayOf(
            "Sun",
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat"
        )
        week_option.adapter=ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, weekdays)
        repeat_option.adapter=ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Intervaloptions)
        repeat_option.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.e("Options2", Intervaloptions.toString())
                if(Intervaloptions.get(position)=="Daily"){
                    scheduleType="D"
                    datefield.visibility=View.GONE
                    schedule_month.visibility=View.GONE
                    weekly_schedule.visibility=View.GONE
                }
                else if(Intervaloptions.get(position)=="Weekly")
                {
                    scheduleType="W"
                    datefield.visibility=View.GONE
                    schedule_month.visibility=View.GONE
                    weekly_schedule.visibility=View.VISIBLE
                    week_option.onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            parentView: AdapterView<*>?,
                            selectedItemView: View,
                            position: Int,
                            id: Long
                        ) {
                            scheduleTiming=weekdays.get(position)
                        }

                        override fun onNothingSelected(parentView: AdapterView<*>?) {
                           scheduleTiming=null
                        }
                    }
                }
                else if(Intervaloptions.get(position)=="Monthly")
                {
                    scheduleType="M"
                    datefield.visibility=View.GONE
                    schedule_month.visibility=View.VISIBLE
                    weekly_schedule.visibility=View.GONE
                    scheduleTiming=findViewById<EditText>(R.id.month_date).text.toString()
                }
                else if(Intervaloptions.get(position)=="Yearly")
                {
                    scheduleType="Y"
                    datefield.visibility=View.VISIBLE
                    schedule_month.visibility=View.GONE
                    weekly_schedule.visibility=View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@CreditDebitTransaction, "Please Select Account to transfer", Toast.LENGTH_LONG).show()
            }

        }

        scheduled = findViewById(R.id.schedule_switch)
        scheduled.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                datefield.visibility=View.GONE
                select_schedule.visibility=View.VISIBLE
                schedule_month.visibility=View.GONE
                weekly_schedule.visibility=View.GONE
            }
            else{
                datefield.visibility=View.VISIBLE
                select_schedule.visibility=View.GONE
                schedule_month.visibility=View.GONE
                weekly_schedule.visibility=View.GONE
                scheduleType=null
            }
        }


        viewModel.getSubAccountList()
        viewModel.accountList.observe(this, androidx.lifecycle.Observer {
            subaccList=it
            Log.e("CreditDebitOption Ob", it.toString())
            options = ArrayList()
            for (i in subaccList){
                options.add(i.name)
            }

                spinner = findViewById(R.id.selectaccount)
                Log.e("CreditDebit Options", options.toString())
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

//        for (i in subaccList){
//            options.add(i.name)
//        }



//        if(transactiontype=="T") {
//            spinner = findViewById(R.id.selectaccount)
//            Log.e("CreditDebit Options", options.toString())
//            spinner.adapter=ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)
//            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    toBeTransferAcc=subaccList.get(position).id
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    Toast.makeText(this@CreditDebitTransaction, "Please Select Account to transfer", Toast.LENGTH_LONG).show()
//                    toBeTransferAcc=null
//                }
//
//            }
//        }

        var submit=findViewById<Button>(R.id.add)
        submit.setOnClickListener {
            var isschedule = scheduled.isChecked
            var amount = findViewById<EditText>(R.id.amount).text.toString().toDouble()
            var desc = findViewById<EditText>(R.id.description).text.toString()
//            var date = findViewById<EditText>(R.id.date).text.toString()

            var datetext=date.text.toString()
            if (amount > 0 && desc != "" && datetext!="") {
                if(transactiontype=="C") {
                    viewModel.creditmoney(isschedule= isschedule, amount = amount, desc, datetext, orderByDate, interval = scheduleType, timing = scheduleTiming)
                    Toast.makeText(this, "Amount Credited", Toast.LENGTH_LONG).show()
                }
                else if(transactiontype=="D"){
                    viewModel.debitmoney(isschedule= isschedule, amount, desc, datetext, orderByDate, interval = scheduleType, timing = scheduleTiming)
                    Toast.makeText(this, "Amount Debited", Toast.LENGTH_LONG).show()
                }
                else
                    if(transactiontype=="T")
                    {
                        if(toBeTransferAcc!=null) {
                            viewModel.transfermoney(isschedule= isschedule, amount, desc, datetext, toBeTransferAcc!!, orderByDate, interval = scheduleType, timing = scheduleTiming)
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
        orderByDate=year.toString()+"/"
        if(month<9)
            orderByDate+="0"+(month+1)+"/"
        else
            orderByDate+=(month+1).toString()+"/"
        if(day<10)
            orderByDate+="0"+(day)
        else
            orderByDate+=day
    }
}