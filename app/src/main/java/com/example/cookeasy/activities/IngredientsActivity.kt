package com.example.cookeasy.activities

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookeasy.R
import com.example.cookeasy.adapters.IngredientsAdapter
import com.example.cookeasy.objects.IngredientItem
import com.example.cookeasy.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_ingredients.*
import kotlinx.android.synthetic.main.enter_ingredient.*
import kotlinx.android.synthetic.main.enter_ingredient.view.*
import android.R.menu
import android.content.Intent
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.*
import android.widget.Toast
import com.example.cookeasy.adapters.GroceriesAdapter
import com.example.cookeasy.objects.GroceryItem
import kotlinx.android.synthetic.main.activity_groceries.*
import kotlinx.android.synthetic.main.activity_ingredients.recyclerView
import kotlinx.android.synthetic.main.grocery_item.*
import kotlinx.android.synthetic.main.grocery_item.itemName
import kotlinx.android.synthetic.main.ingredient_item.*


class IngredientsActivity : AppCompatActivity() {

    var users: User? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference


    private lateinit var rView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var ingredientList = ArrayList<IngredientItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)

        database = Firebase.database.reference

        val uid = FirebaseAuth.getInstance().currentUser!!.uid


        generateIngredientList(250)
    }


    private fun generateIngredientList(size: Int) {
        val nameList = ArrayList<String>()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val ref = FirebaseDatabase.getInstance().getReference("/ingredients/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
//               val adapter = IngredientsAdapter<RecyclerView.ViewHolder>()


                p0.children.forEach {
                    Log.d("in foreach", "hi")
//                    Log.d("ingredient", it.child("ingredient").getValue().toString())
                    Log.d("ingredient", it.child("name").getValue(String::class.java).toString())
                    val ingredient = it.child("name").getValue(String::class.java).toString()
                    if(ingredient != null) {
                        val item = IngredientItem(ingredient.toString())
                        ingredientList.add(item)
                    }
                }

                recyclerView.adapter = IngredientsAdapter(ingredientList)
                recyclerView.layoutManager = LinearLayoutManager(this@IngredientsActivity)
                recyclerView.setHasFixedSize(true)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

//        return list
    }

    fun deleteItemFromView(view: View) {
        val index = ingredientList.indexOf(IngredientItem(itemName.text.toString()))
        recyclerView.adapter = IngredientsAdapter(ingredientList)
        ingredientList.remove(IngredientItem(itemName.text.toString()))
        val adapter = recyclerView.adapter as IngredientsAdapter
        adapter.removeItem()
        recyclerView.layoutManager = LinearLayoutManager(this@IngredientsActivity)
        recyclerView.setHasFixedSize(true)
        Log.d("delete", "button clicked!")
    }

    fun dialogView(view: View) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.enter_ingredient, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Enter the ingredient")
        val alertDialog = builder.show()

        alertDialog.submitItem.setOnClickListener {
            val ingredientInput = dialogView.name.text.toString()
            if(ingredientInput != ""){
                writeNewIngredient(ingredientInput)
                val item = IngredientItem(ingredientInput)
                ingredientList.add(item)
                recyclerView.adapter = IngredientsAdapter(ingredientList)
                recyclerView.layoutManager = LinearLayoutManager(this@IngredientsActivity)
                recyclerView.setHasFixedSize(true)
                alertDialog.dismiss()
            }
        }
    }

    private fun writeNewIngredient(ingredientName: String) {
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ingredient = IngredientItem(ingredientName)
//        FirebaseDatabase.getInstance().getReference("/users/$uid").setValue(user)
        FirebaseDatabase.getInstance().getReference("/ingredients/$uid").push().setValue(ingredient)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ingredientMenuItem -> {
                Toast.makeText(applicationContext, "ingredient item clicked", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.groceryMenuItem ->{
                val intent = Intent(this, GroceriesActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.logoutMenuItem ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
