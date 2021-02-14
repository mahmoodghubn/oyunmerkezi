package com.example.oyunmerkezi3.model

import java.util.*

data class Comment(
    var message: String,
    var userId:String,
    var date: Date
) {
    constructor() : this("","",Calendar.getInstance().time)

    constructor(userId: String,message: String) : this(message,userId,Calendar.getInstance().time)
}
