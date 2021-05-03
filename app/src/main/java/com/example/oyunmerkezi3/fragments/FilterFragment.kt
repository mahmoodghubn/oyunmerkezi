package com.example.oyunmerkezi3.fragments

/**
 * this class create a FilterGame object and returns it to GameFragment
 */

import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.database.Category
import com.example.oyunmerkezi3.database.Language
import com.example.oyunmerkezi3.databinding.BottomSheetBinding
import com.example.oyunmerkezi3.databinding.FragmentFilterBinding
import com.example.oyunmerkezi3.utils.GameFilter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.oyunmerkezi3.bottomSheet.CategoryAdapter


class FilterFragment : Fragment() {

    private lateinit var listView: ListView
    private var minPrice: Int? = null
    private var maxPrice: Int? = null
    private var minHours: Int? = null
    private var maxHours: Int? = null
    private var orderBy: Int? = null
    private var age: Int? = null
    private var category: Category? = null
    private var language: Language? = null
    private var playersNo: Int? = null
    private var radioGroup: RadioGroup? = null
    private var gameRate: Float? = null
    private var publishDate: Int? = null
    private var offline: Boolean? = null
    private var inStock: Boolean? = null
    private lateinit var binding: FragmentFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_filter, container, false
        )

        binding.categoryTextView.setOnClickListener(View.OnClickListener {
            setCategory()
        })
        binding.languageTextView.setOnClickListener(View.OnClickListener {
            setLanguage()
        })

        binding.orderByTextView.setOnClickListener(View.OnClickListener {
            setOrder()
        })

        binding.categoryButton.setOnClickListener(View.OnClickListener {
            setCategory()
        })
        binding.languageButton.setOnClickListener(View.OnClickListener {
            setLanguage()
        })

        binding.orderButton.setOnClickListener(View.OnClickListener {
            setOrder()
        })
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
            offline = if (binding.online.isChecked) {
                true
            } else {
                null
            }
            inStock = if (binding.online.isChecked) {
                true
            } else {
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
                inStock,
                gameRate,
                category,
                language,
                publishDate,
                orderBy
            )

            val args = Bundle()
            args.putParcelable("filter", filter)
            this.findNavController().setGraph(R.navigation.navigation, args)
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setOrder() {
        val bottomSheet = BottomSheetDialog(this.requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetBinding>(
            layoutInflater,
            R.layout.bottom_sheet,
            null,
            false
        )
        listView = bindingSheet.categoryListView
        val listItems: ArrayList<String> = resources.getStringArray(
            R.array.filter
        ).toCollection(ArrayList())


        val adapter1 = CategoryAdapter(requireContext(), listItems)
        listView.adapter = adapter1
        bottomSheet.setContentView(bindingSheet.root)
        bottomSheet.show()
        listView.setOnItemClickListener { _, _, position, _ ->

            val selectedOrder: String = listItems[position]
            binding.chosenOrder.text = selectedOrder
            orderBy = position
            bottomSheet.hide()
        }
    }

    private fun setLanguage() {
        val bottomSheet = BottomSheetDialog(this.requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetBinding>(
            layoutInflater,
            R.layout.bottom_sheet,
            null,
            false
        )
        listView = bindingSheet.categoryListView
        val listItems = arrayListOf<String>()
        for (item in Language.values()) {
            listItems.add(item.name)
        }

        val adapter1 = CategoryAdapter(requireContext(), listItems)
        listView.adapter = adapter1
        bottomSheet.setContentView(bindingSheet.root)
        bottomSheet.show()
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedLanguage: String = listItems[position]
            binding.chosenLanguage.text = selectedLanguage
            language = Language.values()[position]
            bottomSheet.hide()
        }
    }

    private fun setCategory() {
        val bottomSheet = BottomSheetDialog(this.requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetBinding>(
            layoutInflater,
            R.layout.bottom_sheet,
            null,
            false
        )
        listView = bindingSheet.categoryListView
        val listItems = arrayListOf<String>()
        for (item in Category.values()) {
            listItems.add(item.name)
        }

        val adapter1 = CategoryAdapter(requireContext(), listItems)
        listView.adapter = adapter1
        bottomSheet.setContentView(bindingSheet.root)
        bottomSheet.show()
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory: String = listItems[position]
            binding.chosenCategory.text = selectedCategory
            category = Category.values()[position]
            bottomSheet.hide()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.filter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.reset) {
            minPrice = null
            maxPrice = null
            minHours = null
            maxHours = null
            age = null
            playersNo = null
            offline = null
            inStock = null
            gameRate = null
            category = null
            language = null
            publishDate = null
            orderBy = null
            binding.let {
                it.minPrice.setText("")
                it.maxPrice.setText("")
                it.minimumHours.setText("")
                it.maximumHours.setText("")
                it.ageValue.setText("")
                it.playersValue.setText("")
                it.online.isChecked = false
                it.inStock.isChecked = false
                it.gameRating.rating = 0F
                binding.radioGroupDate.clearCheck()
                binding.chosenLanguage.text = ""
                binding.chosenOrder.text = ""
                binding.chosenCategory.text = ""
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
