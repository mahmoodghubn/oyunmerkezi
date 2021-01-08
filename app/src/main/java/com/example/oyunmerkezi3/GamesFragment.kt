package com.example.oyunmerkezi3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.oyunmerkezi3.database.GameDatabase
import com.example.oyunmerkezi3.database.GamesViewModel
import com.example.oyunmerkezi3.database.GamesViewModelFactory
import com.example.oyunmerkezi3.databinding.FragmentGamesBinding
import com.example.oyunmerkezi3.recycling.GameAdapter
import com.example.oyunmerkezi3.recycling.GameListener

class GamesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding: FragmentGamesBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_games, container, false
        )

        //x value comes from orderBy fragment to determine the order of the games in games list
        val x:Int = this.arguments?.getInt("x") ?:0
        val application = requireNotNull(this.activity).application
        val dataSource = GameDatabase.getInstance(application).gameDatabaseDao
        val viewModelFactory = GamesViewModelFactory(dataSource, application,x)

        val gamesViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(GamesViewModel::class.java)

        binding.lifecycleOwner = this
        binding.gamesViewModel = gamesViewModel

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

        val adapter = GameAdapter(GameListener { game ->
            gamesViewModel.onGameClicked(game)
        })

        //observing the navigateToDetails valeu in gamesViewModel to navigate to detail view when clicking on a game
        gamesViewModel.navigateToDetails.observe(viewLifecycleOwner, Observer { game ->
            game?.let {
                this.findNavController().navigate(
                    GamesFragmentDirections
                        .actionGamesFragmentToDetailActivity(game)
                )
                gamesViewModel.onGameDetailsNavigated()
            }
        })

        binding.gameLest.adapter = adapter
        gamesViewModel.games?.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    // Creating our Share Intent
    private fun getShareIntent(intent: Intent) {
        startActivity(intent)
    }
}
