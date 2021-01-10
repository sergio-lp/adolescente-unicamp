package com.nrgbrainn.adolescentes.models

import com.google.firebase.firestore.DocumentId

class Content(
    @DocumentId
    var id: String? = null,
    var title: String? = null,
    var subtitle: String? = null,
    var image: String? = null,
    var url: String? = null
) {
    companion object Status {
        const val STATUS_LOCKED = 0
        const val STATUS_UNLOCKED = 1
        const val STATUS_DONE = 2
    }

    var status: Int? = null
    var unitId: String? = null
    var userId: String? = null
    var color: String? = null
}