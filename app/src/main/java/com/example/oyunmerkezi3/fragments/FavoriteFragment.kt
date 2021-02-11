package com.example.oyunmerkezi3.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.oyunmerkezi3.MainActivity
import com.example.oyunmerkezi3.R
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.oyunmerkezi3.bottomSheet.GamePriceAdapter
import com.example.oyunmerkezi3.database.*
import com.example.oyunmerkezi3.databinding.FragmentFavoriteBinding
import com.example.oyunmerkezi3.recycling.GameAdapter
import com.example.oyunmerkezi3.recycling.GameListener
import com.google.android.material.bottomsheet.BottomSheetBehavior

class FavoriteFragment : Fragment() {
    private lateinit var adapter1: GamePriceAdapter
    private lateinit var adapter: GameAdapter
    private lateinit var gamesViewModel: GamesViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding: FragmentFavoriteBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_favorite, container, false
        )
        val activity: MainActivity = activity as MainActivity
        gamesViewModel = activity.gamesViewModel

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.priceBottomSheet.parentView)
        val soldGameListView = binding.priceBottomSheet.selectedGameRecyclerView

        activity.bottomSheetFunction(
            bottomSheetBehavior,
            soldGameListView,
            binding.priceBottomSheet,
            container
        )

        activity.sendWhatsAppMessage(binding.sendButton)

        adapter =
            GameAdapter(
                GameListener { game -> gamesViewModel.onGameClicked(game) },
                gamesViewModel,
                binding.priceBottomSheet.root
            )
        binding.gameList.adapter = adapter
        gamesViewModel.favoriteGames.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        gamesViewModel.totalPriceLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.priceBottomSheet.total.text = it.toString()
            }
        })
        binding.priceBottomSheet.clearImageButton.setOnClickListener {
            gamesViewModel.clearTheListOfSelectedGame()
            soldGameListView.adapter = null

        }

        //observing the navigateToDetails value in gamesViewModel to navigate to detail view when clicking on a game
        gamesViewModel.navigateToDetails.observe(viewLifecycleOwner, Observer { game ->
            game?.let {
                this.findNavController().navigate(
                    FavoriteFragmentDirections
                        .actionFavoriteFragmentToDetailActivity(game)
                )
                gamesViewModel.onGameDetailsNavigated()
            }
        })

        binding.lifecycleOwner = this

        return binding.root

    }

}


