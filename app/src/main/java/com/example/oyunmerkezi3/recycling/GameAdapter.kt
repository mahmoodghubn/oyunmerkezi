package com.example.oyunmerkezi3.recycling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.oyunmerkezi3.database.Game
import com.example.oyunmerkezi3.database.GamesViewModel
import com.example.oyunmerkezi3.database.MiniGame
import com.example.oyunmerkezi3.databinding.ListItemViewBinding
import com.example.oyunmerkezi3.databinding.PriceBottomSheetBinding

class GameAdapter(private val clickListener: GameListener,private val gamesViewModel: GamesViewModel,private val coordinatorLayout: CoordinatorLayout) :
    ListAdapter<Game, GameAdapter.ViewHolder>(GameDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position)!!, gamesViewModel,coordinatorLayout)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            clickListener: GameListener,
            item: Game,
            gamesViewModel: GamesViewModel,
            coordinatorLayout: CoordinatorLayout
        ) {

            binding.sellingCheckBox.isChecked =
                gamesViewModel.sellingCheckBox.filter{ it.gameId == item.gameId }.size == 1

            binding.buyingCheckBox.isChecked =
                gamesViewModel.buyingCheckBox.filter{ it.gameId == item.gameId }.size == 1


            binding.sellingCheckBox.setOnClickListener { view ->
                gamesViewModel.addSoledGame(MiniGame( item.gameId,item.gameName,item.sellingPrice,item.platform))
                coordinatorLayout.visibility = View.VISIBLE


            }
            binding.buyingCheckBox.setOnClickListener { view ->
                gamesViewModel.addBoughtGame(MiniGame(item.gameId,item.gameName,item.buyingPrice,item.platform))
                coordinatorLayout.visibility = View.VISIBLE
            }

            binding.game = item
            binding.clickListener = clickListener
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

class GameListener(val clickListener: (game: Game) -> Unit) {
    fun onClick(game: Game) = clickListener(game)
}