package com.example.oyunmerkezi3.model

import com.example.oyunmerkezi3.utils.CalendarUtil
import com.example.oyunmerkezi3.utils.Date


data class Comment(
    var message: String?,
    var gameRate:Int,
    var userId:String,
    var date: Date,
    var userName:String?,
    var photoUri: String
) {
    constructor() : this("",0,"",CalendarUtil(null).getCurrentDate(),"","")

    constructor(userId: String,gameRate: Int,userName: String?,message: String,photoUri: String) : this(message,gameRate,userId,CalendarUtil(null).getCurrentDate(),userName,photoUri)
}
