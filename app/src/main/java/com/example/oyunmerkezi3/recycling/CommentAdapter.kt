package com.example.oyunmerkezi3.recycling

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.oyunmerkezi3.databinding.CommentItemBinding
import com.example.oyunmerkezi3.model.Comment

class CommentAdapter(
    private val clickListener: CommentListener) :
    ListAdapter<Comment, CommentAdapter.ViewHolder>(CommentDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            clickListener: CommentListener,
            item: Comment
        ) {

            binding.comment = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = CommentItemBinding
                    .inflate(layoutInflater, parent, false)

                return ViewHolder(view)
            }
        }
    }

    class CommentDiffCallback :
        DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.message == newItem.message
        }

        override fun getChangePayload(oldItem: Comment, newItem: Comment): Any {
            return newItem
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }
}

class CommentListener(val clickListener: (comment: Comment) -> Unit) {
    fun onClick(comment: Comment) = clickListener(comment)
}