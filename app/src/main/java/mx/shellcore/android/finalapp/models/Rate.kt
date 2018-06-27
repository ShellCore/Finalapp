package mx.shellcore.android.finalapp.models

import java.util.*

data class Rate (
        val text : String,
        val rate : Float,
        val createdAt : Date,
        val profileImageUrl : String = ""
)