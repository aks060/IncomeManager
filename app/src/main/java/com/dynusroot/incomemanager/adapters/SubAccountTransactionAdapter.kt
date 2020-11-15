package com.dynusroot.incomemanager.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.database.models.transactions
import java.text.SimpleDateFormat
import java.time.ZoneId

class SubAccountTransactionAdapter(
    d: ArrayList<transactions>,
    val context: Context
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var data: ArrayList<transactions>

    init {
        data= ArrayList()
        data=d
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return viewholder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_transaction,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder)
        {
            is viewholder -> {
                holder.bind(data.get(position), position)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val main=itemView.findViewById<ConstraintLayout>(R.id.mainmain)
        var type=itemView.findViewById<TextView>(R.id.type)
        val amount=itemView.findViewById<TextView>(R.id.amount)
        var description=itemView.findViewById<TextView>(R.id.description)
        var amount_after=itemView.findViewById<TextView>(R.id.amount_after)
        val date=itemView.findViewById<TextView>(R.id.date)

        @SuppressLint("ResourceAsColor")
        fun bind(b: transactions, position: Int)
        {
            var act=""
            if(b.type=="D")
            {
                main.setBackgroundColor(R.color.c2)
                main.setBackgroundResource(R.color.c2)
                type.text="Debited"
                amount.text="Rs -"+b.amount.toString()
            }
            else
                if(b.type=="C")
            {
                main.setBackgroundColor(R.color.c5)
                main.setBackgroundResource(R.color.c5)
                type.text="Credited"
                amount.text="Rs +"+b.amount.toString()
            }
            else if(b.type=="T")
                {
                    main.setBackgroundColor(R.color.c7)
                    main.setBackgroundResource(R.color.c7)
                    type.text="Transfered"
                    amount.text="Rs +"+b.amount.toString()
                }
            description.text=b.description
            date.text=b.date
            amount_after.text=b.amountafter.toString()
        }
    }
}