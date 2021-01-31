package com.example.oyunmerkezi3.bottomSheet

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.database.GamesViewModel
import com.example.oyunmerkezi3.database.MiniGame

class GamePriceAdapter(
    private val context: Context,
    private val dataSource: ArrayList<MiniGame>,
    private val gamesViewModel: GamesViewModel,
    private val isSelling: Boolean
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_prices_bottom_sheet, parent, false)
            holder = ViewHolder()
            holder.platformTextView = view.findViewById(R.id.platform) as TextView
            holder.gameNameTextView = view.findViewById(R.id.game_name) as TextView
            holder.gamePriceTextView = view.findViewById(R.id.game_price) as TextView
            holder.countTextView = view.findViewById(R.id.count) as TextView
            holder.subTotalTextView = view.findViewById(R.id.sub_total) as TextView
            holder.closeButton = view.findViewById(R.id.close_button) as ImageButton
            holder.addButton = view.findViewById(R.id.add_image_button) as ImageButton
            holder.removeButton = view.findViewById(R.id.remove_image_button) as ImageButton

            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val platformTextView = holder.platformTextView
        val gameNameTextView = holder.gameNameTextView
        val gamePriceTextView = holder.gamePriceTextView
        val countTextView = holder.countTextView
        val subTotalTextView = holder.subTotalTextView
        val closeButton = holder.closeButton
        val addButton = holder.addButton
        val removeButton = holder.removeButton
        val game = getItem(position) as MiniGame

        addButton.setOnClickListener {
            game.count = game.count.plus(1)
            game.total = game.price * game.count
            countTextView.text = game.count.toString()
            subTotalTextView.text = game.total.toString()
            gamesViewModel.increaseCount(game,isSelling)
        }

        removeButton.setOnClickListener {
            if (game.count != 0) {
                game.count = game.count.minus(1)
                game.total = game.price * game.count
                countTextView.text = game.count.toString()
                subTotalTextView.text = game.total.toString()
                gamesViewModel.decreasingCount(game,isSelling)
            }
        }

        closeButton.setOnClickListener {
            if (isSelling) {
                gamesViewModel.addSoledGame(game)

            } else {
                gamesViewModel.addBoughtGame(game)
            }
            this.notifyDataSetChanged()
        }

        platformTextView.text = game.platform.toString()
        gameNameTextView.text = game.gameName
        gamePriceTextView.text = game.price.toString()
        subTotalTextView.text = game.total.toString()
        countTextView.text = game.count.toString()

        return view

    }

    private class ViewHolder {
        lateinit var platformTextView: TextView
        lateinit var gameNameTextView: TextView
        lateinit var gamePriceTextView: TextView
        lateinit var countTextView: TextView
        lateinit var subTotalTextView: TextView
        lateinit var closeButton: ImageButton
        lateinit var addButton: ImageButton
        lateinit var removeButton: ImageButton
    }
}