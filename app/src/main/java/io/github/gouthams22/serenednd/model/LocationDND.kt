package io.github.gouthams22.serenednd.model

class LocationDND(
    private val latitude: Float,
    private val longitude: Float,
    val name: String = ""
) {
    val coordinateText: String
        get() = "$latitude, $longitude"
}