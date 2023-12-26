package com.github.jetjinser.tendance.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Device(
    val name: String,
    val address: String,
) : Parcelable