package com.github.jetjinser.tendance.service.ble

import java.util.Locale

object GattAttributes {
    private val attributes: HashMap<String, String> = HashMap()

    private const val tendanceService = "6A43804F-E691-45E5-B161-CBD4A0C00700"

    init {
        // Services.
        attributes[tendanceService] = "Tendance Screen Service"

        // Characteristics.
        attributes["B93BAB34-66F4-4F08-A3C1-38C0BD700EB6"] = "Upload Image"
    }

    fun lookup(uuid: String, defaultName: String): String {
        val name = attributes[uuid.uppercase(Locale.ROOT)]
        return name ?: defaultName
    }
}