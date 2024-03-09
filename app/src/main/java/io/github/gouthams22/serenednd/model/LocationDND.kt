package io.github.gouthams22.serenednd.model

class LocationDND(
    val latitude: Double,
    val longitude: Double,
    val name: String = ""
) {
    val coordinateText: String
        get() = "$latitude, $longitude"
}