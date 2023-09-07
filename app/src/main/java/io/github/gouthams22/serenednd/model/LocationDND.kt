package io.github.gouthams22.serenednd.model

class LocationDND(
    private val latitude: Double,
    private val longitude: Double,
    val name: String = ""
) {
    val coordinateText: String
        get() = "$latitude, $longitude"
}