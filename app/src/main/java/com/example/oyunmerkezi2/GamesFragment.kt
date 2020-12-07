package com.example.oyunmerkezi2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.oyunmerkezi2.GamesFragmentDirections
import com.example.oyunmerkezi2.R
import com.example.oyunmerkezi2.database.Game
import com.example.oyunmerkezi2.databinding.FragmentGamesBinding

class GamesFragment : Fragment() {

    private lateinit var game: Game

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_games, container, false)
        val binding: FragmentGamesBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_games, container, false
        )
        //The complete onClickListener with Navigation using createNavigateOnClickListener
        /*binding.playButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_gamesFragment_to_detailFragment)
        )*/
        //passing argument to detail fragment
        game = Game(gameName="call of duty",sellingPrice = 99,buyingPrice = 70)
        binding.playButton.setOnClickListener { v: View ->
            v.findNavController()
                .navigate(
                    GamesFragmentDirections.actionGamesFragmentToDetailFragment(
                        game
                    )
                )
        }
        val number = "+905465399410"
        val url = "https://api.whatsapp.com/send?phone=$number"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        if (null == i.resolveActivity(requireActivity().packageManager)) {
            binding.sendButton.visibility = View.GONE
            //TODO need test by uninstalling whatsapp

        }
        binding.sendButton.setOnClickListener(){
            getShareIntent(i)


        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    // Creating our Share Intent
    private fun getShareIntent(intent :Intent) {

        startActivity(intent)
    }
}
