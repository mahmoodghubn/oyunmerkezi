package com.example.oyunmerkezi3.recycling

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.oyunmerkezi3.databinding.VideoItemViewBinding

class VideoAdapter(private val clickListener: VideoListener)
    : RecyclerView.Adapter<VideoAdapter.ViewHolder>(){

    var data = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clickListener, data[position])
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
        override fun getItemCount() = data.size
}

class VideoListener(val clickListener: (game: String) -> Unit) {
    fun onClick(game: String) = clickListener(game)
}