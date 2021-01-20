package com.example.oyunmerkezi3.bottomSheet
/*
* this class is responsible for getting a list and show it as bottom sheet
* and returns the selected item to filter fragment */
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.oyunmerkezi3.R

//this class for category, language and orders as well
class CategoryAdapter(private val context: Context,
                    private val dataSource: ArrayList<String>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
            view = inflater.inflate(R.layout.bottom_sheet_item, parent, false)
            holder = ViewHolder()
            holder.categoryTextView = view.findViewById(R.id.category_text) as TextView
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val detailTextView = holder.categoryTextView
        val  category = getItem(position) as String
        detailTextView.text = category
        return view

    }

    private class ViewHolder {
        lateinit var categoryTextView: TextView
    }
}