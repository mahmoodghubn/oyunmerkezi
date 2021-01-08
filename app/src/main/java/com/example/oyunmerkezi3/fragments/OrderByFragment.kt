package com.example.oyunmerkezi3.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.databinding.FragmentOrderByBinding
import kotlin.properties.Delegates


class OrderByFragment : Fragment() {
    private var radioGroup: RadioGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentOrderByBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_order_by, container, false
        )
        var x by Delegates.notNull<Int>()
        radioGroup = binding.radioGroup

        radioGroup!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { _, checkedId ->
            x = when (checkedId) {
                R.id.alphabetic -> 1
                R.id.alphabetic_re -> 2
                R.id.newer -> 3
                R.id.older -> 4
                R.id.expensive -> 5
                R.id.cheap -> 6
                R.id.hours -> 7
                R.id.hours_re -> 8
                R.id.high_rate -> 9
                else -> {
                    0
                }

            }
            val args = Bundle()
            args.putInt("x", x)
            this.findNavController().setGraph(R.navigation.navigation, args)
        })
        return binding.root
    }

}