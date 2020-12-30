package com.example.oyunmerkezi3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.oyunmerkezi3.databinding.FragmentDetailBinding


class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private lateinit var viewModel: DetailViewModel
    private lateinit var viewModelFactory: DetailViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail,
            container,
            false
        )
        val detailFragmentArgs by navArgs<DetailFragmentArgs>()

        //passing the variable from the games fragment to detail fragment by using view model factory to initialize view model
        viewModelFactory =
            DetailViewModelFactory(detailFragmentArgs.game)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)

        //using data binding and avoid using ui controller to get data from view model
        binding.detailViewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root

    }
}