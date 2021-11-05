package com.dynusroot.incomemanager.backupclass

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class accountsBackup: Serializable {
    var id:Long=0
    var name:String=""
    var description:String=""
    var totalBalance:Double= 0.0
    var createdAt: String= SimpleDateFormat("YYYY-MM-dd HH:MM:SS z").format(Date())

    constructor(id:Long, name:String, description: String, totalBalance:Double, createdAt: String){
        this.id=id
        this.name=name
        this.description=description
        this.totalBalance=totalBalance
        this.createdAt=createdAt
    }
}

class subaccountsBackup: Serializable {
    var id:Long=0
    var name: String=""
    var parentaccount: Long=0
    var description:String=""
    var balance:Double=0.0

    constructor(id:Long, name:String, parentaccount: Long, description:String, balance: Double){
        this.id=id
        this.name=name
        this.description=description
        this.parentaccount=parentaccount
        this.balance=balance
    }
}

class transactionsBackup: Serializable {
    var id:Long=0
    var type:String=""
    var subaccountID: Long=0
    var amount: Double=0.0
    var description:String=""
    var amountafter: Double?=null
    var transferto: Long=0
    var orderBydate: String=""
    var date:String=""

    constructor(
        id:Long,
        type:String,
        subaccountID: Long,
        amount: Double,
        description:String,
        amountafter: Double,
        transferto: Long,
        orderBydate: String,
        date: String){
        this.id=id
        this.type=type
        this.description=description
        this.subaccountID=subaccountID
        this.amount=amount
        this.amountafter=amountafter
        this.transferto=transferto
        this.orderBydate=orderBydate
        this.date=date
    }
}