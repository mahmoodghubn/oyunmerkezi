package com.example.oyunmerkezi3.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import com.example.oyunmerkezi3.DetailViewModel
import com.example.oyunmerkezi3.DetailViewModelFactory
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private lateinit var viewModelFactory: DetailViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityDetailBinding>(this, R.layout.activity_detail)

        val detailActivityArgs by navArgs<DetailActivityArgs>()

        //passing the variable from the games fragment to detail fragment by using view model factory to initialize view model
        viewModelFactory =
            DetailViewModelFactory(detailActivityArgs.game)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)

        //using data binding and avoid using ui controller to get data from view model
        binding.detailViewModel = viewModel
        binding.lifecycleOwner = this
    }
}