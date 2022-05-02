package com.example.shopassist

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class Activity3 : AppCompatActivity() {

    val dataBase by lazy { FirebaseDatabase.getInstance(getString(R.string.serverUrl)) }
    val myRef by lazy { dataBase.getReference("/") }

    val etName: EditText by lazy { findViewById(R.id.edprdName) }
    val etQuantity: EditText by lazy { findViewById(R.id.edprdQuantity) }
    val etSize: EditText by lazy { findViewById(R.id.edprdSize) }
    val etNote: EditText by lazy { findViewById(R.id.edprdNote) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu?.removeItem(R.id.miAddNewProduct)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        var rc = true
        when (id){
            R.id.miExitApp -> exit()
            else -> rc = super.onOptionsItemSelected(item)
        }
        return rc
    }

    private fun exit(addSuccess: Boolean = false) {
        val rtrndRsponse: Intent = Intent()
        with (rtrndRsponse) {
            if(addSuccess){
                data = Uri.parse("Product added successfully")
            }
            else{
                data = Uri.parse("exit")
            }
            setResult(RESULT_OK, this)
        }
        finish()
    }


    fun btnAddClick(view: View) {
        val typedName: String = etName.text.toString().trim()
        val typedQuantity: String = etQuantity.text.toString().trim()
        val typedQuantityInt: Int
        val typedSize: String = etSize.text.toString().trim()
        val typedNote: String = etNote.text.toString().trim()

        if (typedName.isEmpty()) {
            Toast.makeText(baseContext,getString(R.string.errAdd),Toast.LENGTH_LONG).show()
            etfocus(etName)
            return
        }

        if (typedQuantity.isEmpty()) {
            Toast.makeText(baseContext,getString(R.string.errAdd),Toast.LENGTH_LONG).show()
            etfocus(etQuantity)
            return
        }
        else{
            typedQuantityInt =  typedQuantity.toInt()
            if (typedQuantityInt == 0){
                Toast.makeText(baseContext,getString(R.string.errAdd),Toast.LENGTH_LONG).show()
                etfocus(etQuantity)
                return
            }
        }

        if (typedSize.isEmpty()) {
            Toast.makeText(baseContext,getString(R.string.errAdd),Toast.LENGTH_LONG).show()
            etfocus(etSize)
            return
        }

        if (typedNote.isEmpty()) {
            Toast.makeText(baseContext,getString(R.string.errAdd),Toast.LENGTH_LONG).show()
            etfocus(etNote)
            return
        }

        val ky = myRef.child("products").push().key
        val prod = Product(typedName, typedQuantityInt, typedSize, typedNote)
        myRef.child("products").child(ky!!).setValue(prod).addOnFailureListener(object: OnFailureListener {
            override fun onFailure(p0: Exception) {
                Toast.makeText(baseContext,getString(R.string.errAddFireBase),Toast.LENGTH_LONG).show()
            }
        })

        exit(addSuccess = true)
    }


    fun etfocus(where: EditText) {
        where.isFocusable
        where.isFocusableInTouchMode = true
        where.requestFocus()
    } //etfocus


}
