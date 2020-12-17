package edu.uw.jrrose.photogram


data class Image (
    var imageUrl: String?,
    var imageCaption: String?,
    var uid: String?,
    var likes: MutableMap<String, Boolean>?
) {
    constructor() : this(null, null, null, null)
}
