package com.example.oyunmerkezi3.recycling

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.oyunmerkezi3.database.Game
import com.example.oyunmerkezi3.databinding.VideoItemViewBinding


class VideoAdapter(private val clickListener: VideoListener) :
    ListAdapter<String, VideoAdapter.ViewHolder>(VideoDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position)!!)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: VideoItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            clickListener: VideoListener,
            item: String
        ) {
            binding.video = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val view = VideoItemViewBinding
                    .inflate(layoutInflater, parent, false)

                return ViewHolder(view)
            }
        }
    }

    class VideoDiffCallback :
        DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}

class VideoListener(val clickListener: (game: String) -> Unit) {
    fun onClick(game: String) = clickListener(game)
}