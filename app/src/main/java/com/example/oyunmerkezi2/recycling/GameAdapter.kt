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
        holder.bind(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(itemView: View): RecyclerView.ViewHolder(itemView){
        val gameNameTextView:TextView = itemView.findViewById(R.id.game_name)
        val gamePriceTextView:TextView = itemView.findViewById(R.id.game_price)
        fun bind(
        item: Game
        ) {
            gameNameTextView.text = item.gameName
            gamePriceTextView.text = item.sellingPrice.toString()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.list_item_view, parent, false)

                return ViewHolder(view)
            }
        }
    }


}

