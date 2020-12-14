package edu.uw.jrrose.photogram

import android.net.Uri

data class Image (
    var imageUrl: String?,
    var imageCaption: String?,
    var uid: String?,
    var likes: Map<String, Boolean>?
) {
    constructor() : this(null, null, null, null)
}