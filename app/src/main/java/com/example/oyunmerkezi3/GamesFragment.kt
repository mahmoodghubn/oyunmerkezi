package com.example.oyunmerkezi3

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.oyunmerkezi3.database.GamesViewModel
import com.example.oyunmerkezi3.databinding.FragmentGamesBinding
import com.example.oyunmerkezi3.recycling.GameAdapter
import com.example.oyunmerkezi3.recycling.GameListener
import com.example.oyunmerkezi3.service.NotificationService
import com.example.oyunmerkezi3.utils.ConnectionBroadcastReceiver
import com.example.oyunmerkezi3.utils.GameFilter
import com.example.oyunmerkezi3.utils.NotificationTask
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GamesFragment : Fragment() {
    private lateinit var adapter: GameAdapter
    private lateinit var gamesViewModel: GamesViewModel
    private lateinit var binding: FragmentGamesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_games, container, false
        )
        val user = Firebase.auth.currentUser
        if (user == null) {
            launchSignInFlow()
        }

        //TODO do not call this every time we start the application
        // only if the service has been killed

        //notification and service
        val showNotification = Intent(requireContext(), NotificationService::class.java)
        showNotification.action = NotificationTask().actionShowNotification
        NotificationService.enqueueWork(requireContext(), showNotification)

        val activity: MainActivity = activity as MainActivity
        gamesViewModel = activity.gamesViewModel
        val platformSharedPreferences = activity.platformSharedPreferences
        val editor: SharedPreferences.Editor = activity.editor

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.priceBottomSheet.parentView)
//        bottomSheetBehavior.isDraggable = false
        val selectedGameRecyclerView = binding.priceBottomSheet.selectedGameRecyclerView

        activity.bottomSheetFunction(
            bottomSheetBehavior,
            selectedGameRecyclerView,
            binding.priceBottomSheet,
            container
        )

        performFiltering()

        inflateChips(platformSharedPreferences, binding, editor)

        activity.sendWhatsAppMessage(binding.sendButton)

        adapter =
            GameAdapter(
                GameListener { game -> gamesViewModel.onGameClicked(game) },
                gamesViewModel,
                binding.priceBottomSheet.root
            )
        binding.gameList.adapter = adapter
        gamesViewModel.games2.observe(viewLifecycleOwner, { it ->
            it?.let {
                adapter.submitList(it)
            }
        })

        var firstCreation = true
        ConnectionBroadcastReceiver.registerToFragmentAndAutoUnregister(
            activity,
            this,
            gamesViewModel.games2,
            object : ConnectionBroadcastReceiver() {
                override fun onConnectionChanged(hasConnection: Boolean) {
                    if (!hasConnection || !firstCreation) {
                        firstCreation = false
                        showInternetStatus(hasConnection)
                    }
                }
            })
        gamesViewModel.totalPriceLiveData.observe(viewLifecycleOwner, {
            it?.let {
                binding.priceBottomSheet.total.text = it.toString()
            }
        })
        binding.priceBottomSheet.clearImageButton.setOnClickListener {
            gamesViewModel.clearTheListOfSelectedGame()
            selectedGameRecyclerView.adapter = null
        }

        //observing the navigateToDetails value in gamesViewModel to navigate to detail view when clicking on a game
        gamesViewModel.navigateToDetails.observe(viewLifecycleOwner, { game ->
            game?.let {
                this.findNavController().navigate(
                    GamesFragmentDirections
                        .actionGamesFragmentToDetailActivity(game)
                )
                gamesViewModel.onGameDetailsNavigated()
            }
        })

        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun inflateChips(
        platformSharedPreferences: SharedPreferences,
        binding: FragmentGamesBinding,
        editor: SharedPreferences.Editor
    ) {
        val platformsArray: Array<String> = resources.getStringArray(R.array.platforms)
        val activePlatforms = arrayListOf<String>()
        platformSharedPreferences.let { it1 ->
            for (item in platformsArray) {
                if (it1.getBoolean(item, true))
                    activePlatforms.add(item)
            }
        }
        val chipGroup = binding.platformList
        val inflater2 = LayoutInflater.from(chipGroup.context)
        val currentPlatform = platformSharedPreferences.getString("current", "PS4")

        val children: List<Chip>
        if (activePlatforms.size > 1) {
            children = activePlatforms.map { regionName ->
                val chip = inflater2.inflate(R.layout.platform, chipGroup, false) as Chip
                chip.text = regionName
                chip.tag = regionName
                chipGroup.addView(chip)
                if (regionName == currentPlatform) {
                    chip.isChecked = true
                }
                chip.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        editor.putString("current", button.text as String)
                        editor.apply()
                    }
                }
                chip
            }
            chipGroup.removeAllViews()
            for (chip in children) {
                chipGroup.addView(chip)
            }
        }
    }

    private fun performFiltering() {
        val filter: GameFilter? = this.arguments?.getParcelable<GameFilter>("filter")
        filter?.let { gamesViewModel.filter(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
        val item = menu.findItem(R.id.search)
        val searchView: SearchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                adapter.filter.filter(newText)
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.reset) {
            gamesViewModel.filter(null)
        }
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    fun showInternetStatus(connected: Boolean) {
        if (connected) {
            val snackBar =
                Snackbar.make(binding.sendButton, "connected", Snackbar.LENGTH_LONG)
            snackBar.animationMode = ANIMATION_MODE_SLIDE
            snackBar.setBackgroundTint(GREEN)
            snackBar.show()
        } else {
            val snackBar =
                Snackbar.make(binding.sendButton, "disconnected", Snackbar.LENGTH_LONG)
            snackBar.duration = 10000
            snackBar.setBackgroundTint(RED)
            snackBar.animationMode = ANIMATION_MODE_SLIDE
            snackBar.show()
        }
    }

    fun launchSignInFlow() {
        // Give users the option to sign in / register with their email or Google account.
        // If users choose to register with their email,
        // they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
//            AuthUI.IdpConfig.FacebookBuilder().build()

            // This is where you can provide more ways for users to register and
            // sign in.
        )

        // Create and launch sign-in intent.
        // We listen to the response of this activity with the
        // SIGN_IN_REQUEST_CODE
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            GamesFragment.SIGN_IN_REQUEST_CODE
        )
    }

    companion object {
        const val SIGN_IN_REQUEST_CODE = 1001
        const val TAG = "GamesFragment"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                Log.i(
                    TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }
}