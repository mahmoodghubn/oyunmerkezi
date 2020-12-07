package com.example.oyunmerkezi2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.oyunmerkezi2.DetailFragmentArgs
import com.example.oyunmerkezi2.DetailViewModel
import com.example.oyunmerkezi2.DetailViewModelFactory
import com.example.oyunmerkezi2.R
import com.example.oyunmerkezi2.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private lateinit var viewModel: DetailViewModel
    private lateinit var viewModelFactory: DetailViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail,
            container,
            false
        )
        //getting the argument from the games fragment
        //val args = DetailFragmentArgs.fromBundle(requireArguments())
        val detailFragmentArgs by navArgs<DetailFragmentArgs>()

        //passing the variable from the games fragment to detail fragment by using view model factory to initialize view model
        viewModelFactory =
            DetailViewModelFactory(detailFragmentArgs.game)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)

        //using data binding and avoid using ui controller to get data from view model
        binding.detailViewModel = viewModel
        binding.lifecycleOwner = this

//        return inflater.inflate(R.layout.fragment_detail, container, false)
        return binding.root

    }
}