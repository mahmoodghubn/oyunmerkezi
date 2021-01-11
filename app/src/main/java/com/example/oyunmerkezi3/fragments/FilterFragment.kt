package com.example.oyunmerkezi3.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.database.Category
import com.example.oyunmerkezi3.database.Language
import com.example.oyunmerkezi3.databinding.FragmentFilterBinding
import com.example.oyunmerkezi3.utils.GameFilter


class FilterFragment : Fragment() {

    private var minPrice: Int? = null
    private var maxPrice: Int? = null
    private var minHours: Int? = null
    private var maxHours: Int? = null
    private var age: Int? = null
    private var category: Category? = null
    private var language: Language? = null
    private var playersNo: Int? = null
    private var radioGroup: RadioGroup? = null
    private var gameRate: Float? = null
    private var publishDate: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentFilterBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_filter, container, false
        )

        val adapter = this.context?.let { it1 ->
            ArrayAdapter<Category>(
                it1,
                android.R.layout.simple_spinner_item,
                Category.values()
            )
        }
        binding.categorySpinner.adapter = adapter
        binding.categorySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                category = Category.values()[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                category = null
            }
        }

        val adapter2 = this.context?.let { it1 ->
            ArrayAdapter<Language>(
                it1,
                android.R.layout.simple_spinner_item,
                Language.values()
            )
        }
        binding.languageSpinner.adapter = adapter2
        binding.languageSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                language = Language.values()[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                language = null
            }
        }

        radioGroup = binding.radioGroupDate
        radioGroup!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { _, checkedId ->
            publishDate = when (checkedId) {
                R.id.week -> 1
                R.id.month -> 2
                R.id.year -> 3
                else -> {
                    null
                }
            }
        })

        binding.gameRating.onRatingBarChangeListener =
            OnRatingBarChangeListener { _, rating, _ ->
                gameRate = rating
            }

        binding.apply.setOnClickListener() {
            val text = binding.minPrice.text.toString()
            if (text != "") {
                minPrice = Integer.parseInt(text)
            }

            val text2 = binding.maxPrice.text.toString()
            if (text2 != "") {
                maxPrice = Integer.parseInt(text2)
            }

            val text3 = binding.minimumHours.text.toString()
            if (text3 != "") {
                minHours = Integer.parseInt(text3)
            }

            val text4 = binding.maximumHours.text.toString()
            if (text4 != "") {
                maxHours = Integer.parseInt(text4)
            }

            val text5 = binding.ageValue.text.toString()
            if (text5 != "") {
                age = Integer.parseInt(text5)
            }

            val text6 = binding.playersValue.text.toString()
            if (text6 != "") {
                playersNo = Integer.parseInt(text6)
            }

            // we do not want to pass false value because do not need false value and
            // we didn't handle false case so we only need true or null
            val offline: Boolean? = if (binding.online.isChecked) {
                true
            }else{
                null
            }
            val inStock: Boolean? = if (binding.online.isChecked) {
                true
            }else{
                null
            }
            val favorite: Boolean? = if (binding.favorite.isChecked) {
                true
            }else{
                null
            }
            val filter = GameFilter(
                minPrice,
                maxPrice,
                minHours,
                maxHours,
                age,
                playersNo,
                offline,
                favorite,
                inStock,
                gameRate,
                category,
                language,
                publishDate
            )

            val args = Bundle()
            args.putParcelable("filter", filter)
            this.findNavController().setGraph(R.navigation.navigation, args)
        }

        return binding.root
    }
}
