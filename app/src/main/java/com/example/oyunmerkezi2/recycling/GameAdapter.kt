package com.example.oyunmerkezi2.recycling

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oyunmerkezi2.R

import com.example.oyunmerkezi2.database.Game

class GameAdapter : RecyclerView.Adapter<TextItemViewHolder>() {

    var data = listOf<Game>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item.gameName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.text_item_view, parent, false) as TextView

        return TextItemViewHolder(view)
    }
}
class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)
