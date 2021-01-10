package com.nrgbrainn.adolescentes.models

import com.google.firebase.firestore.DocumentId

class Quiz(
    @DocumentId
    val id: String? = null,
    val question: String? = null,
    val alternatives: ArrayList<String>? = null,
    val correct: Int? = null
) {
    var unitId: Int? = null
    var contentId: Int? = null
}