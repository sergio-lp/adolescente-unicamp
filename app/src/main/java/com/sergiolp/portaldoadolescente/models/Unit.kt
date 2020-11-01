package com.sergiolp.portaldoadolescente.models

import com.google.firebase.firestore.DocumentId

class Unit(
    @DocumentId
    val id: String? = null,
    val title: String? = null,
    val icon: String? = null,
    val color: String? = null
)