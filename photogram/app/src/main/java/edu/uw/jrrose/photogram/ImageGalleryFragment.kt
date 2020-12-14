package edu.uw.jrrose.photogram

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ImageGalleryFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var database: DatabaseReference
//    private lateinit var options: FirebaseRecyclerOptions<Image>
//    private lateinit var rootView: View

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

        database = Firebase.database.reference
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


        val options = FirebaseRecyclerOptions.Builder<Image>()
            .setQuery(database, Image::class.java)
            .build()

        val adapter = FirebaseImageAdapter(options)
        val recycler: RecyclerView = rootView.findViewById<RecyclerView>(R.id.images_list)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        onStart(adapter)
        observeAuthenticationState(rootView, adapter)

        return rootView
    }
    fun onStart(adapter: FirebaseImageAdapter) {
        super.onStart();
        adapter.startListening();
    }
//    Similarly, the stopListening() call removes the event listener and all data in the adapter. Call this method when the containing Activity or Fragment stops:

    fun onStop(adapter: FirebaseImageAdapter) {
        super.onStop();
        adapter.stopListening();
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        observeAuthenticationState(view)
//    }

    private fun observeAuthenticationState(rootView: View, adapter: FirebaseImageAdapter) {

        val fab: FloatingActionButton = rootView.findViewById(R.id.fab)
        Log.v(TAG, "In authenticationState")
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    fab.show()
                    val fab: View = rootView.findViewById(R.id.fab)
                    fab.setOnClickListener {
                        onStop(adapter)
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

    inner class FirebaseImageAdapter(options: FirebaseRecyclerOptions<Image>) : FirebaseRecyclerAdapter<Image, FirebaseImageAdapter.ViewHolder>(
        options
    ) {
//        private val options: FirebaseRecyclerOptions<Image>

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById<ImageView>(R.id.image_item)
            val caption: TextView = view.findViewById<TextView>(R.id.caption_item)
            val imageBtn: ImageButton = view.findViewById<ImageButton>(R.id.like_btn)
            val numberOfLikes: TextView = view.findViewById(R.id.amount_of_likes)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.image_list_item, parent, false)
                return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Image) {
            // Bind the Chat object to the ChatHolder
            // ...
            holder.caption.text = model.imageCaption
            Glide.with(this@ImageGalleryFragment)
                .load(model.imageUrl)
                .into(holder.imageView)
            val imageLikes: String = if (model.likes?.size  == null) {
                "Likes: 0"
            } else {
                "Likes: ${model.likes!!.size}"
            }
            holder.numberOfLikes.text = imageLikes
        }
    }
}
