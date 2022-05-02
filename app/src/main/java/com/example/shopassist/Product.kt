package com.example.shopassist


data class Product(var name: String, var reqNum: Int, var sizeType: String, var note: String)
{
    private var ky:String=""
    constructor() : this("", 0, "", "")

    override fun toString(): String {
        return "$name  |  $reqNum  |  $sizeType \n $note"
    }


    fun fillKy(x:String)
    {
        this.ky=x
    }

    fun giveKy():String
    {
        return this.ky
    }

}
