package edu.uw.jrrose.photogram

import android.content.res.ColorStateList
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ImageGalleryFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var database: DatabaseReference

    companion object {
        const val TAG = "ImageGalleryFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_image_gallery, container, false)

        database = Firebase.database.reference

        val options = FirebaseRecyclerOptions.Builder<Image>()
            .setQuery(database, Image::class.java)
            .build()

        val adapter = FirebaseImageAdapter(options)
        observeAuthenticationState(rootView, adapter)
        val recycler: RecyclerView = rootView.findViewById<RecyclerView>(R.id.images_list)
        recycler.layoutManager = LinearLayoutManager(context)
        val topSpacingDecoration = TopSpacingItemDecoration(30)
        recycler.addItemDecoration(topSpacingDecoration)
        recycler.adapter = adapter



        onStart(adapter)
        return rootView
    }
    fun onStart(adapter: FirebaseImageAdapter) {
        super.onStart();
        adapter.startListening();
    }

    fun onStop(adapter: FirebaseImageAdapter) {
        super.onStop();
        adapter.stopListening();
    }

    private fun observeAuthenticationState(rootView: View, adapter: FirebaseImageAdapter) {

        val fab: FloatingActionButton = rootView.findViewById(R.id.fab)
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    // Refresh the page once logged in
                    onStop(adapter)
                    onStart(adapter)
                    fab.show() // Show add image btn
                    fab.setOnClickListener {
                        onStop(adapter)
                        findNavController().navigate(R.id.UploadScreenFragment)
                    }
                    Log.v(TAG, "logged in") // delete later
                }
                else -> {
                    fab.hide()
                    Log.v(TAG, it.toString())
                }
            }
        })
    }

    inner class FirebaseImageAdapter(options: FirebaseRecyclerOptions<Image>) : FirebaseRecyclerAdapter<Image, FirebaseImageAdapter.ViewHolder>(
        options
    ) {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById<ImageView>(R.id.image_item)
            val caption: TextView = view.findViewById<TextView>(R.id.caption_item)
            val imageBtn: ImageButton = view.findViewById<ImageButton>(R.id.like_btn)
            val numberOfLikes: TextView = view.findViewById(R.id.amount_of_likes)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.image_list_item, parent, false)
                return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Image) {
            var userUid: String? = null
            if (FirebaseAuth.getInstance().currentUser != null) {
                userUid = FirebaseAuth.getInstance().currentUser?.uid
            }

            holder.caption.text = model.imageCaption
            Glide.with(this@ImageGalleryFragment)
                .load(model.imageUrl)
                .into(holder.imageView)

            val imageRef = getRef(position)
            if (userUid != null) {
                holder.imageBtn.setOnClickListener {
                    if (model.likes != null) { // if pic has likes
                        if(isLiked(model, userUid)) { // if pic is already liked
                            model.likes!!.remove(userUid)
                            imageRef.setValue(model)
                        } else { // if pic isn't already liked
                            model.likes!!.put(userUid, true)
                            imageRef.setValue(model)
                        }
                    } else { // if pic has no likes
                        Log.v(TAG, "imageref: $imageRef") // delete later
                        val imageLikes = mutableMapOf(userUid to true)
                        model.likes = imageLikes
                        imageRef.setValue(model)
                        Log.v(TAG, "${model.likes}")
                    }
                }

                // Color of like btn
                if (model.likes != null) { // if pic is already liked
                    if (isLiked(model, userUid)) { // Check if one of the likes is from the user
                        holder.imageBtn.imageTintList = ColorStateList.valueOf(context!!.getColor(R.color.colorAccent))
                    } else {
                        holder.imageBtn.imageTintList = ColorStateList.valueOf(context!!.getColor(R.color.light_gray))
                    }
                }
                else { // if pic isn't liked at all
                    holder.imageBtn.imageTintList = ColorStateList.valueOf(context!!.getColor(R.color.light_gray))
                }

                // Delete image on long press
                holder.imageView.setOnLongClickListener {
                    if (userUid == model.uid) {
                     imageRef.setValue(null)
                    }
                    true
                }
            }

            // Likes text
            val imageLikes: String = if (model.likes?.size  == null) {
                "0"
            } else {
                "${model.likes!!.size}"
            }
            holder.numberOfLikes.text = imageLikes
        }

        private fun isLiked(model: Image, userUid: String): Boolean {
            for((k,v) in model.likes!!) {
                if(k == userUid) {
                    return true
                }
            }
            return false
        }
    }
}
