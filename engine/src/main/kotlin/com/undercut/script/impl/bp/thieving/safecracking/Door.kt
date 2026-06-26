package com.undercut.script.impl.bp.thieving.safecracking
import com.undercut.game.tileOfLocal

import world.gregs.voidps.type.Tile
import com.undercut.traversal.nodes.DoorInfo

enum class Door(
    val realIdOpen: Int,
    val realIdClosed: Int,
    val locationOpen: Tile,
    val locationClosed: Tile,
    val tileInside: Tile,
    val tileOutside: Tile,
    val openAction: String? = "Open"
) {
    // Minimap: 0 Tile.of(2757, 3503, 0) local: tileOfLocal(5, 47, 0) -- in front of large door
    // CombinedLocationSection: realId: 25638 visibleId: 25638 name: Large door tile: [ X: 2757, Y: 3503, Z: 0 ] -- closed
    // Location: realId: 25639 visibleId: 25639 name: Large door tile: [ X: 2757, Y: 3504, Z: 0 ] -- open
    // Minimap: 0 Tile.of(2757, 3504, 0) local: tileOfLocal(5, 48, 0) -- inside large door
    CAMELOT_CASTLE_MAIN_DOOR(
        25639,
        25638,
        Tile.of(2757, 3504, 0),
        Tile.of(2757, 3503, 0),
        Tile.of(2757, 3504, 0),
        Tile.of(2757, 3503, 0),
        "Open"
    ),

    // Minimap: 0 Tile.of(2750, 3504, 0) local: tileOfLocal(62, 48, 0)
    // CombinedLocationSection: realId: 25642 visibleId: 25642 name: Door tile: [ X: 2750, Y: 3503, Z: 0 ]
    // Location: realId: 25643 visibleId: 25643 name: Door tile: [ X: 2750, Y: 3504, Z: 0 ]
    // Minimap: 0 Tile.of(2750, 3503, 0) local: tileOfLocal(62, 47, 0)
    CAMELOT_CASTLE_WEST_DOOR(
        25643,
        25642,
        Tile.of(2750, 3504, 0),
        Tile.of(2750, 3503, 0),
        Tile.of(2750, 3504, 0),
        Tile.of(2750, 3503, 0),
        "Open"
    ),

    // CombinedLocationSection: realId: 34807 visibleId: 34807 name: Door tile: [ X: 2659, Y: 3319, Z: 0 ]
    // Location: realId: 34808 visibleId: 34808 name: Door tile: [ X: 2659, Y: 3320, Z: 0 ]
    ARDOUGNE_SQUARE_NORTH_LOWER(
        34808,
        34807,
        Tile.of(2659, 3320, 0),
        Tile.of(2659, 3319, 0),
        Tile.of(2659, 3320, 0),
        Tile.of(2659, 3319, 0),
        "Open"
    ),

    // CombinedLocationSection: realId: 34811 visibleId: 34811 name: Door tile: [ X: 2661, Y: 3320, Z: 1 ]
    // Location: realId: 34813 visibleId: 34813 name: Door tile: [ X: 2660, Y: 3320, Z: 1 ]
    ARDOUGNE_SQUARE_NORTH_UPPER(
        34813,
        34811,
        Tile.of(2660, 3320, 1),
        Tile.of(2661, 3320, 1),
        Tile.of(2660, 3320, 1),
        Tile.of(2661, 3320, 1),
        "Open"
    ),

    // CombinedLocationSection: realId: 34807 visibleId: 34807 name: Door tile: [ X: 2652, Y: 3302, Z: 0 ]
    // Location: realId: 34808 visibleId: 34808 name: Door tile: [ X: 2651, Y: 3302, Z: 0 ]
    ARDOUGNE_SQUARE_SOUTH_WEST_LOWER(
        34808,
        34807,
        Tile.of(2651, 3302, 0),
        Tile.of(2652, 3302, 0),
        Tile.of(2651, 3302, 0),
        Tile.of(2652, 3302, 0),
        "Open"
    ),

    // CombinedLocationSection: realId: 34811 visibleId: 34811 name: Door tile: [ X: 2648, Y: 3300, Z: 1 ]
    // Location: realId: 34813 visibleId: 34813 name: Door tile: [ X: 2649, Y: 3300, Z: 1 ]
    ARDOUGNE_SQUARE_SOUTH_WEST_UPPER(
        34813,
        34811,
        Tile.of(2649, 3300, 1),
        Tile.of(2648, 3300, 1),
        Tile.of(2649, 3300, 1),
        Tile.of(2648, 3300, 1),
        "Open"
    ),

    // CombinedLocationSection: realId: 2548 visibleId: 2548 name: Door tile: [ X: 2580, Y: 3297, Z: 0 ]
    // Location: realId: 2549 visibleId: 2549 name: Door tile: [ X: 2579, Y: 3297, Z: 0 ]
    ARDOUGNE_CASTLE_LOWER_MAIN(
        2549,
        2548,
        Tile.of(2579, 3297, 0),
        Tile.of(2580, 3297, 0),
        Tile.of(2579, 3297, 0),
        Tile.of(2580, 3297, 0),
        "Open"
    ),

    // CombinedLocationSection: realId: 34807 visibleId: 34807 name: Door tile: [ X: 2572, Y: 3303, Z: 0 ]
    // Location: realId: 34808 visibleId: 34808 name: Door tile: [ X: 2572, Y: 3302, Z: 0 ]
    ARDOUGNE_CASTLE_LOWER_NORTH(
        34808,
        34807,
        Tile.of(2572, 3302, 0),
        Tile.of(2572, 3303, 0),
        Tile.of(2572, 3303, 0),
        Tile.of(2572, 3302, 0),
        "Open"
    ),

    // CombinedLocationSection: realId: 34807 visibleId: 34807 name: Door tile: [ X: 2577, Y: 3305, Z: 1 ]
    // Location: realId: 34808 visibleId: 34808 name: Door tile: [ X: 2577, Y: 3306, Z: 1 ]
    ARDOUGNE_CASTLE_UPPER_NORTH(
        34808,
        34807,
        Tile.of(2577, 3306, 1),
        Tile.of(2577, 3305, 1),
        Tile.of(2577, 3305, 1),
        Tile.of(2577, 3306, 1),
        "Open"
    ),

    // CombinedLocationSection: realId: 34807 visibleId: 34807 name: Door tile: [ X: 2574, Y: 3290, Z: 1 ]
    // Location: realId: 34808 visibleId: 34808 name: Door tile: [ X: 2573, Y: 3290, Z: 1 ]
    ARDOUGNE_CASTLE_UPPER_SOUTH(
        34808,
        34807,
        Tile.of(2573, 3290, 1),
        Tile.of(2574, 3290, 1),
        Tile.of(2574, 3290, 1),
        Tile.of(2573, 3290, 1),
        "Open"
    ),

    // CombinedLocationSection: realId: 17089 visibleId: 17089 name: Large door tile: [ X: 2537, Y: 3089, Z: 0 ]
    // Location: realId: 17090 visibleId: 17090 name: Large door tile: [ X: 2537, Y: 3090, Z: 0 ]
    YANILLE_WALL_DOOR(
        17090,
        17089,
        Tile.of(2537, 3090, 0),
        Tile.of(2537, 3089, 0),
        Tile.of(2537, 3089, 0),
        Tile.of(2537, 3090, 0),
        "Open"
    ),

    // CombinedLocationSection: realId: 1533 visibleId: 1533 name: Door tile: [ X: 2551, Y: 3082, Z: 0 ]
    // Location: realId: 1534 visibleId: 1534 name: Door tile: [ X: 2551, Y: 3083, Z: 0 ]
    YANILLE_BAR_DOOR(
        1534,
        1533,
        Tile.of(2551, 3083, 0),
        Tile.of(2551, 3082, 0),
        Tile.of(2551, 3082, 0),
        Tile.of(2551, 3083, 0),
        "Open"
    ),

}

/**
 * Extension function to convert Door enum to DoorInfo for use with the traversal system
 */
fun Door.toDoorInfo(): DoorInfo = DoorInfo(
    realIdOpen = this.realIdOpen,
    realIdClosed = this.realIdClosed,
    locationOpen = this.locationOpen,
    locationClosed = this.locationClosed,
    tileInside = this.tileInside,
    tileOutside = this.tileOutside,
    openAction = this.openAction
)
