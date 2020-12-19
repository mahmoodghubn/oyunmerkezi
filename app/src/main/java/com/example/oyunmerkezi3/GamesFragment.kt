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

//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val value = dataSnapshot.child("game").children//getValue<Game>()
//                for (element in value) {
//                    var game: Game = element.getValue() as Game
//                    Log.d("dfsfffffffffffffffffs2", "Value is: ${game.gameName}")
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//        myRef.setValue("Hello, World!")
        val application = requireNotNull(this.activity).application
        val dataSource = GameDatabase.getInstance(application).gameDatabaseDao
        val viewModelFactory = GamesViewModelFactory(dataSource, application)



        val gamesViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(GamesViewModel::class.java)
        binding.lifecycleOwner = this

        binding.gamesViewModel = gamesViewModel


        val number = "+905465399410"
        val url = "https://api.whatsapp.com/send?phone=$number"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (null == intent.resolveActivity(requireActivity().packageManager)) {
            binding.sendButton.visibility = View.GONE
            //TODO need test by uninstalling whatsapp

        }
        binding.sendButton.setOnClickListener(){
            getShareIntent(intent)


        }
        val adapter = GameAdapter(GameListener { gameId ->
            gamesViewModel.onGameClicked(gameId)
        })
        gamesViewModel.navigateToDetails.observe(viewLifecycleOwner, Observer { game ->
            game?.let {
                this.findNavController().navigate(
                    GamesFragmentDirections
                        .actionGamesFragmentToDetailFragment(game)
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
