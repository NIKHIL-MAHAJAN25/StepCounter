package com.nikhil.stepcounter.dataclasses

import com.google.firebase.Timestamp

data class Steps (
    val date: String = "",
    val timestamp: Timestamp= Timestamp.now(),
    val totalsteps:Int?=0,
    val dailygoal:Int?=10000,
    val calories:Int?=0,
    val distance:Int?=0,

)