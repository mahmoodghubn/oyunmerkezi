package com.example.oyunmerkezi3.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.oyunmerkezi3.GamesFragmentDirections
import com.example.oyunmerkezi3.MainActivity
import com.example.oyunmerkezi3.R
import android.content.Intent
import android.net.Uri
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.ListView
import androidx.constraintlayout.widget.ConstraintLayout
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
    private lateinit var adapter2: GamePriceAdapter
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
        val soldGameListView = binding.priceBottomSheet.soldListView
        val boughtGameListView = binding.priceBottomSheet.boughtListView

        bottomSheetFunction(
            bottomSheetBehavior,
            soldGameListView,
            boughtGameListView,
            activity,
            binding,
            container
        )
        sendWhatsAppMessage(binding)

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
            boughtGameListView.adapter = null

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
    private fun bottomSheetFunction(
        bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>,
        listView1: ListView,
        listView2: ListView,
        activity: MainActivity,
        binding: FragmentFavoriteBinding,
        container: ViewGroup?
    ) {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //inflate the chosen games in the bottom sheet
                        adapter1 = GamePriceAdapter(
                            requireContext(),
                            gamesViewModel.sellingCheckBoxArray,
                            gamesViewModel,
                            true
                        )
                        listView1.adapter = adapter1
                        adapter2 = GamePriceAdapter(
                            requireContext(),
                            gamesViewModel.buyingCheckBoxArray,
                            gamesViewModel,
                            false
                        )
                        listView2.adapter = adapter2
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        val gesture = GestureDetector(
            activity,
            object : SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                override fun onFling(
                    e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (binding.priceBottomSheet.root.visibility == View.VISIBLE) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    return super.onFling(e1, e2, velocityX, velocityY)
                }
            })
        container!!.setOnTouchListener { v, event ->
            if (event!!.action == MotionEvent.ACTION_UP)
                v!!.performClick()

            gesture.onTouchEvent(event)
        }
    }

    private fun sendWhatsAppMessage(binding: FragmentFavoriteBinding) {
        //whatsApp button
        val number = "+905465399410"
        val url = "https://api.whatsapp.com/send?phone=$number"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (null == intent.resolveActivity(requireActivity().packageManager)) {
            binding.sendButton.visibility = View.GONE
            //TODO need test by uninstalling whatsapp
        }

        binding.sendButton.setOnClickListener() {
            getShareIntent(intent)
        }
    }

    // Creating our Share Intent
    private fun getShareIntent(intent: Intent) {
        startActivity(intent)
    }

}


