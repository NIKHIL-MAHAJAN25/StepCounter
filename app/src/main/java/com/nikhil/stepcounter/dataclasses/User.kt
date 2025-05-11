package com.nikhil.stepcounter.dataclasses

data class User (
    var authid:String?=null,
    val name:String?=null,
    val age:Int?=null,
    val gender:Int?=null,
    val weight:Int?=null,
    val data: ArrayList<Steps> = arrayListOf()
)