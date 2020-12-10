package edu.uw.jrrose.photogram

import android.accounts.AuthenticatorException
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser

class ImageGalleryFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_image_gallery, container, false)
//        Log.v(TAG, "In onCreateView")
//        val fab: FloatingActionButton = rootView.findViewById(R.id.fab)
//        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
//            when (authenticationState) {
//                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
//                    fab.show()
//                    Log.v(TAG, "logged in")
//                }
//                else -> {
//                    fab.hide()
//                    Log.v(TAG, "logged out")
//                }
//            }
//        })
        observeAuthenticationState(rootView)

        return rootView
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        observeAuthenticationState(view)
//    }

    private fun observeAuthenticationState(rootView: View) {

        val fab: FloatingActionButton = rootView.findViewById(R.id.fab)
        Log.v(TAG, "In authenticationState")
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    fab.show()
                    val fab: View = rootView.findViewById(R.id.fab)
                    fab.setOnClickListener {

                        findNavController().navigate(R.id.UploadScreenFragment)
                    }
                    Log.v(TAG, "logged in")
                }
                else -> {
                    fab.hide()
                    Log.v(TAG, it.toString())
                }
            }
        })
    }

    companion object {
        const val TAG = "ImageGalleryFragment"
    }
}