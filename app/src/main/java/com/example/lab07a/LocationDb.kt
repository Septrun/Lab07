package com.example.lab07a

class LocationDb {
    private val locations: List<Location> = listOf(
        Location(1, "Earth (C-137)", "Planet", "Dimension C-137"),
        Location(2, "Abadango", "Cluster", "unknown"),
        Location(3, "Citadel of Ricks", "Space station", "unknown"),
        Location(4, "Worldender's lair", "Planet", "unknown"),
        Location(5, "Anatomy Park", "Microverse", "Dimension C-137"),
        Location(6, "Interdimensional Cable", "TV", "unknown"),
        Location(7, "Immortality Field Resort", "Resort", "unknown"),
        Location(8, "Post-Apocalyptic Earth", "Planet", "Post-Apocalyptic Dimension"),
        Location(9, "Purge Planet", "Planet", "Replacement Dimension"),
        Location(10, "Venzenulon", "Planet", "unknown")
    )

    fun getAllLocations(): List<Location> {
        return locations
    }

    fun getLocationById(id: Int): Location {
        return locations.first { it.id == id }
    }
}