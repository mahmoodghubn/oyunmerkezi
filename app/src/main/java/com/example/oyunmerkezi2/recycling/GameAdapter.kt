package com.example.oyunmerkezi2.recycling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.oyunmerkezi2.R

import com.example.oyunmerkezi2.database.Game
class GameAdapter : ListAdapter<Game, GameAdapter.ViewHolder>(GameDiffCallback()){




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
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

    class GameDiffCallback :
        DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.gameId == newItem.gameId
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem == newItem
        }
    }
}

