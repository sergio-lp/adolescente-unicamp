package com.sergiolp.portaldoadolescente.models

import com.google.firebase.firestore.DocumentId

class User(
    @DocumentId
    val id: String? = null,
    val username: String? = null,
    var photo_url: String? = null,
    var completed_content: ArrayList<String>? = null,
    var score: Int = 0
)