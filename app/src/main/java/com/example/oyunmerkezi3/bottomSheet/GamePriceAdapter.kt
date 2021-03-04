package com.example.oyunmerkezi3.bottomSheet

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.oyunmerkezi3.database.GamesViewModel
import com.example.oyunmerkezi3.database.MiniGame
import com.example.oyunmerkezi3.databinding.ItemPricesBottomSheetBinding

class GamePriceAdapter(
    private val gamesViewModel: GamesViewModel
) :
    RecyclerView.Adapter<GamePriceAdapter.ViewHolder>() {

    var data = listOf<MiniGame>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], gamesViewModel)
        holder.binding.closeButton.setOnClickListener {
            gamesViewModel.addMiniGame(data[position])
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,data.size)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemPricesBottomSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            game: MiniGame,
            gamesViewModel: GamesViewModel
        ) {

            binding.addImageButton.setOnClickListener {
                game.count = game.count.plus(1)
                game.total = game.price * game.count
                binding.count.text = game.count.toString()
                binding.subTotal.text = game.total.toString()
                gamesViewModel.increaseCount(game)
            }

            binding.removeImageButton.setOnClickListener {
                if (game.count != 0) {
                    game.count = game.count.minus(1)
                    game.total = game.price * game.count
                    binding.count.text = game.count.toString()
                    binding.subTotal.text = game.total.toString()
                    gamesViewModel.decreasingCount(game)
                }
            }
            if (game.gameId > 0) {
                binding.count.setTextColor(Color.parseColor("#A5D6A7"))
                binding.platform.setTextColor(Color.parseColor("#A5D6A7"))
                binding.gameName.setTextColor(Color.parseColor("#A5D6A7"))
                binding.gamePrice.setTextColor(Color.parseColor("#A5D6A7"))
                binding.subTotal.setTextColor(Color.parseColor("#A5D6A7"))
            }
            binding.game = game
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ItemPricesBottomSheetBinding
                    .inflate(layoutInflater, parent, false)

                return ViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}
