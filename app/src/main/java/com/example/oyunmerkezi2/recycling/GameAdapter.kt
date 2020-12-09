package com.example.oyunmerkezi2.recycling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oyunmerkezi2.R

import com.example.oyunmerkezi2.database.Game

class GameAdapter : RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    var data = listOf<Game>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.gameNameTextView.text = item.gameName
        holder.gamePriceTextView.text = item.sellingPrice.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.list_item_view, parent, false)

        return ViewHolder(view)
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val gameNameTextView:TextView = itemView.findViewById(R.id.game_name)
        val gamePriceTextView:TextView = itemView.findViewById(R.id.game_price)
    }
}

