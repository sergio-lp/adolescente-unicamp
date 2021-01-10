package com.nrgbrainn.adolescentes.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

class Question(
    @DocumentId
    val id: String? = null,
    val question: String? = null,
    val author_id: String? = null,
    val replied: Boolean? = null,
    val short_reply: String? = null,
    val reply: String? = null,
    val date: Timestamp? = null
)