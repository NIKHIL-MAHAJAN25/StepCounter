package com.nikhil.stepcounter.dataclasses

data class Steps (
    val date:String="",
    val totalsteps:Int?=0,
    val dailygoal:Int?=10000,
    val calories:Int?=0,
    val distance:Int?=0
)