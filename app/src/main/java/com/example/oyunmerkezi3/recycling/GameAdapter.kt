package com.example.oyunmerkezi3.recycling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.database.Game
import com.example.oyunmerkezi3.database.GamesViewModel
import com.example.oyunmerkezi3.database.MiniGame
import com.example.oyunmerkezi3.databinding.ListItemViewBinding
import java.util.*
import kotlin.collections.ArrayList

class GameAdapter(
    private val clickListener: GameListener,
    private val gamesViewModel: GamesViewModel,
    private val view: View
) :
    ListAdapter<Game, GameAdapter.ViewHolder>(GameDiffCallback()), Filterable {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(clickListener, getItem(position)!!, gamesViewModel, view)
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
            view: View
        ) {

            binding.sellingCheckBox.isChecked =
                gamesViewModel.buyingCheckBoxArray.any { it.gameId == item.gameId }
            binding.buyingCheckBox.isChecked =
                gamesViewModel.buyingCheckBoxArray.any { it.gameId == -item.gameId }
            binding.sellingCheckBox.setOnClickListener {
                gamesViewModel.addMiniGame(
                    MiniGame(
                        item.gameId,
                        item.gameName,
                        item.sellingPrice,
                        item.platform,
                        1,
                        item.sellingPrice
                    )
                )
                //the default value of the coordinatorLayout -which is the parent of bottom sheet- is gone
                //we wanna change it when a game selected
                view.visibility = View.VISIBLE
            }
            binding.buyingCheckBox.setOnClickListener {
                gamesViewModel.addMiniGame(
                    MiniGame(
                        -item.gameId,
                        item.gameName,
                        -item.buyingPrice,
                        item.platform,
                        1,
                        -item.buyingPrice
                    )
                )
                view.visibility = View.VISIBLE
            }
            binding.favoriteImageButton.setOnClickListener {
                gamesViewModel.setFavorite(item.gameId)


            }
            if(gamesViewModel.buyingCheckBoxArray.isNotEmpty()){
                view.visibility = View.VISIBLE
            }

            if (item.favorite)
                binding.favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_24)
            else
                binding.favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)

            binding.notificationImageButton.setOnClickListener {
                gamesViewModel.setShowNotification(item.gameId)
            }
            if (item.showNotification)
                binding.notificationImageButton.setImageResource(R.drawable.ic_baseline_doorbell_24)
            else
                binding.notificationImageButton.setImageResource(R.drawable.ic_baseline_circle_notifications_24)

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

        override fun getChangePayload(oldItem: Game, newItem: Game): Any? {
            return newItem
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem == newItem
        }
    }

    private var filterObject: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val arrayAllGames = ArrayList(gamesViewModel.games2.value!!)
            val filterList = ArrayList<Game>()
            if (charSequence.toString().isEmpty()) {
                filterList.addAll(arrayAllGames)
            } else {
                for (game in arrayAllGames) {
                    if (game.gameName.toLowerCase(Locale.ROOT)
                            .contains(charSequence.toString().toLowerCase(Locale.ROOT))
                    ) {
                        filterList.add(game)
                    }
                }
            }
            val filterResult = FilterResults()
            filterResult.values = filterList
            return filterResult
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            this@GameAdapter.submitList(filterResults.values as List<Game>)
        }
    }

    override fun getFilter(): Filter {
        return filterObject
    }
}

class GameListener(val clickListener: (game: Game) -> Unit) {
    fun onClick(game: Game) = clickListener(game)
}