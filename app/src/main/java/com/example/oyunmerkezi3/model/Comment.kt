package com.example.oyunmerkezi3.model

import java.util.*

data class Comment(
    var message: String,
    var date: Date
) {
    constructor() : this("",Calendar.getInstance().time)

    constructor(message: String) : this(message,Calendar.getInstance().time)
}
