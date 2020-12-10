package com.example.oyunmerkezi2.recycling

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.oyunmerkezi2.R

import com.example.oyunmerkezi2.database.Game
import com.example.oyunmerkezi2.databinding.ListItemViewBinding

class GameAdapter : ListAdapter<Game, GameAdapter.ViewHolder>(GameDiffCallback()){




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemViewBinding)
        : RecyclerView.ViewHolder(binding.root){
//        val gameNameTextView:TextView = itemView.findViewById(R.id.game_name)
//        val gamePriceTextView:TextView = itemView.findViewById(R.id.game_price)
        fun bind(
        item: Game
        ) {
//            gameNameTextView.text = item.gameName
//            gamePriceTextView.text = item.sellingPrice.toString()
            binding.game = item
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ListItemViewBinding
                    .inflate(layoutInflater, parent, false)

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

