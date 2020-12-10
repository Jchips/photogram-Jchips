package edu.uw.jrrose.photogram

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass.
 * Use the [UploadScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadScreenFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var navController: NavController

    companion object {
        const val TAG = "UploadScreenFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_upload_screen, container, false)
        observeAuthenticationState()
        return rootView
    }

    private fun observeAuthenticationState() {
        navController = findNavController()
        Log.v(TAG, "In authenticationState")
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {

                }
                else -> {
                    navController.popBackStack()
                }
            }
        })
    }
}