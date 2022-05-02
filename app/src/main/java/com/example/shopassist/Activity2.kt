package com.example.shopassist

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase



class Activity2 : AppCompatActivity() {

    val secret_request_code:Int = 3207

    val dataBase by lazy { FirebaseDatabase.getInstance(getString(R.string.serverUrl)) }
    val myRef by lazy { dataBase.getReference("/") }

    val tvActTitle: TextView by lazy { findViewById(R.id.tvActlisttitle)}
    val lvItems: ListView by lazy { findViewById(R.id.lvProducts) }

    val alProducts = ArrayList<Product>()
    val adptr: ArrayAdapter<Product> by lazy { ArrayAdapter<Product>(this, android.R.layout.simple_list_item_checked, alProducts) }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        lvItems.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        lvItems.setAdapter(adptr)

        changeTitle()    //helper function to change title of activity 2 between "Products List" and "List is empty" depending on listview count

        myRef.child("products").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val prod: Product = snapshot.getValue(Product::class.java) as Product
                prod.fillKy(snapshot.key!!)
                alProducts.add(prod)
                adptr.notifyDataSetChanged()
                lvItems.smoothScrollToPosition(alProducts.size - 1)
                for (i in alProducts.indices)
                    lvItems.setItemChecked(i,false) // to prevent Disruption , not needed if worked in multi selection from beginning
                changeTitle()

            }//onChildAdded

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // TODO("Not yet implemented")
            }//onChildChanged


            override fun onChildRemoved(snapshot: DataSnapshot) { //this fun call by other user
                val prod: Product = snapshot.getValue(Product::class.java) as Product
                prod.fillKy(snapshot.key!!)
                alProducts.remove(prod) // this Not need the Equals method in the class as its data! class
                adptr.notifyDataSetChanged()
                for (i in alProducts.indices)
                    lvItems.setItemChecked(i,false) // to prevent Disruption , not needed if worked in multi selection from beginning
                changeTitle()

            }//onChildRemoved


            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //   TODO("Not yet implemented")
            }//onChildMoved

            override fun onCancelled(prblm: DatabaseError) {
                tvActTitle.setText("unspecify problem raise: $prblm.message");
            }//onCancelled
        })//addChildEventListener
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        var rc = true
        when (id){
            R.id.miExitApp -> exit()
            R.id.miAddNewProduct -> OpenActivity3()
            else -> rc = super.onOptionsItemSelected(item)
        }
        return rc
    }


    private fun exit() {
        val rtrndRsponse: Intent = Intent()
        with (rtrndRsponse) {
            data = Uri.parse("exit")
            setResult(RESULT_OK, this)
        }
        finish()
    }


    fun OpenActivity3() {
        startActivityForResult(Intent("com.example.shopassist.Activity3"), secret_request_code)
    }


    override fun onActivityResult(request_code: Int, result_code: Int, data: Intent?) {
        super.onActivityResult(request_code, result_code, data)
        if (request_code == secret_request_code){
            if (result_code == RESULT_OK){
                Toast.makeText(this, data!!.data.toString(),
                    Toast.LENGTH_LONG).show()
            }
        }
        if (data?.data.toString() == "exit"){
            exit()
        }
        else if (data?.data.toString() != "Product added successfully")
            Toast.makeText(this,"No data come back from the activity!!", Toast.LENGTH_LONG ).show()
    } //onActivityResult}



    fun removeWhatsSelectedMulti(view: View){
        var rslt: String = ""
        if (lvItems.checkedItemCount > 0) {
            for (i in alProducts.indices) {
                if (lvItems.isItemChecked(i)) {
                    //var s:String = alProducts[i].giveKy()
                    //Toast.makeText(this,s, Toast.LENGTH_LONG ).show()
                    myRef.child("products").child(alProducts[i].giveKy()).removeValue()
                    rslt = "Items successfully removed"
                }
            }
        }
        else{
            rslt = "Nothing selected!!"
        }
        Toast.makeText(baseContext, rslt, Toast.LENGTH_LONG).show()
    }



    fun changeTitle(){
        if (lvItems.adapter.count == 0){
            tvActTitle.setText(R.string.emptyList)
        }
        else{
            tvActTitle.setText(R.string.listTitle)
            //Toast.makeText(baseContext, lvItems.adapter.count.toString(), Toast.LENGTH_LONG).show()
        }
    }




}
