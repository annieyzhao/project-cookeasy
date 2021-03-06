package com.example.cookeasy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cookeasy.R
import com.example.cookeasy.objects.GroceryItem
import com.example.cookeasy.objects.IngredientItem
import kotlinx.android.synthetic.main.ingredient_item.view.*

class GroceriesAdapter(private val groceryList: ArrayList<GroceryItem>) : RecyclerView.Adapter<GroceriesAdapter.GroceriesViewHolder>() {

    private var index: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceriesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.grocery_item, parent, false)
        return GroceriesViewHolder(itemView)
    }

    override fun getItemCount() = groceryList.size

    override fun onBindViewHolder(holder: GroceriesViewHolder, position: Int) {
        val currentItem = groceryList[position]
        holder.textView.text = currentItem.name

        holder.button.setOnClickListener {
            index = holder.adapterPosition
            groceryList.removeAt(index)
            notifyDataSetChanged()
        }

    }

    fun removeItem(position: Int) {
        groceryList.removeAt(index)
        notifyDataSetChanged()
//        notifyItemRemoved(index)
//        notifyItemRangeChanged(index, groceryList.size)
    }


    class GroceriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.itemName
        val button: Button = itemView.deleteButton
    }
}