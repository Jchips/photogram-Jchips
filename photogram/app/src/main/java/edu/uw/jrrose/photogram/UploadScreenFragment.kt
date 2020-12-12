package edu.uw.jrrose.photogram

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.File
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [UploadScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadScreenFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var navController: NavController
    private lateinit var rootView: View
    private lateinit var storage: FirebaseStorage
//    private lateinit var photoUri: Uri

    companion object {
        const val TAG = "UploadScreenFragment"
        const val PHOTO_REQUEST_CODE = 2
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_upload_screen, container, false)
        observeAuthenticationState()
        onPickPhoto(rootView)
        storage = Firebase.storage
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

    // Trigger gallery selection for a photo
    fun onPickPhoto(view: View?) {
        // Create intent for picking a photo from the gallery
        val intent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PHOTO_REQUEST_CODE)
        }
    }

    fun loadFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27 && photoUri != null) {
                // on newer versions of Android, use the new decodeBitmap method
                val source: ImageDecoder.Source = ImageDecoder.createSource(requireActivity().contentResolver, photoUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                // support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && requestCode == PHOTO_REQUEST_CODE) {
            val photoUri = data.data!!

            // Load the image located at photoUri into selectedImage
            val selectedImage = loadFromUri(photoUri)

            // Load the selected image into a preview
            val ivPreview: ImageView = rootView.findViewById(R.id.user_thumbnail) as ImageView
            ivPreview.setImageBitmap(selectedImage)
            uploadImage(photoUri)
        }
    }

    private fun uploadImage(photoUri: Uri) {
        val storageRef: StorageReference = storage.reference
//        var file = Uri.fromFile(File("C:\\Users\\jelan\\OneDrive\\Pictures\\Camera Roll"))
        val photoRef = storageRef.child("images/picture${photoUri.lastPathSegment}.jpg")
        val uploadTask = photoRef.putFile(photoUri)

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            Toast.makeText(context, "Image upload error", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
//            val metaData = taskSnapshot.downloadUrl()
            Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
        }.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            photoRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
            } else {
                // Handle failures
                // ...
            }
        }
    }
}