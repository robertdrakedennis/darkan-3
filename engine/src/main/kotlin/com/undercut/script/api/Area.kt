package com.undercut.script.api

import com.undercut.game.Tile
import com.undercut.pathfinder.WorldCollision
import com.undercut.util.random
import java.awt.Point
import java.awt.Polygon
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt


/**
 * Abstract sealed class representing different types of areas in the game world.
 */
sealed class Area {
    
    fun getArea(): Area = this
    
    /**
     * Gets the overlap between this area and another area.
     * @param other The other area to check overlap with
     * @param ignoreZ Whether to ignore the Z (plane) coordinate
     * @return A set of coordinates representing the overlap
     */
    fun getOverlap(other: Area, ignoreZ: Boolean = false): Set<Tile> {
        val otherCoordinates = other.getCoordinates().toSet()
        if (otherCoordinates.isEmpty()) return emptySet()
        
        val result = mutableSetOf<Tile>()
        
        for (thisCoord in getCoordinates()) {
            for (otherCoord in otherCoordinates) {
                if (thisCoord.x == otherCoord.x && 
                    thisCoord.y == otherCoord.y && 
                    (ignoreZ || thisCoord.plane == otherCoord.plane)) {
                    result.add(thisCoord)
                    break
                }
            }
        }
        
        return result
    }
    
    /**
     * Checks if this area overlaps with another locatable.
     * @param other The other locatable to check
     * @param ignoreZ Whether to ignore the Z (plane) coordinate
     * @return true if they overlap, false otherwise
     */
    fun overlaps(other: Tile, ignoreZ: Boolean = false): Boolean {
        return overlaps(other, ignoreZ)
    }
    
    /**
     * Checks if this area overlaps with another area.
     * @param other The other area to check
     * @param ignoreZ Whether to ignore the Z (plane) coordinate
     * @return true if they overlap, false otherwise
     */
    fun overlaps(other: Area, ignoreZ: Boolean = false): Boolean {
        val thisCoords = getCoordinates()
        val otherCoords = other.getCoordinates()
        
        if (thisCoords.isEmpty() || otherCoords.isEmpty()) return false
        
        return thisCoords.any { thisCoord ->
            otherCoords.any { otherCoord ->
                thisCoord.x == otherCoord.x &&
                thisCoord.y == otherCoord.y &&
                (ignoreZ || thisCoord.plane == otherCoord.plane)
            }
        }
    }

    /**
     * Gets a random coordinate from this area.
     * @return A random coordinate from the area
     */
    abstract fun getRandomCoordinate(): Tile
    
    /**
     * Gets a random walkable coordinate from this area.
     * @return A random walkable coordinate, or null if none available
     */
    fun getRandomWalkableCoordinate(): Tile? {
        val walkableCoords = getWalkableCoordinates()
        return if (walkableCoords.isEmpty()) null else walkableCoords[random(walkableCoords.size)]
    }
    
    /**
     * Gets all walkable coordinates in this area.
     * @return A list of walkable coordinates
     */
    fun getWalkableCoordinates(): List<Tile> {
        return getCoordinates().filter { tile ->
            val flags = WorldCollision.getFlags(tile.x.toInt(), tile.y.toInt(), tile.plane.toInt())
            flags == -1 || flags == 0
        }
    }
    
    /**
     * Gets coordinates in this area that are on the same plane as the given coordinate.
     * @param coordinate The reference coordinate
     * @return List of coordinates on the same plane
     */
    fun getCoordinatesInArea(coordinate: Tile): List<Tile> {
        return getCoordinates().filter { it.plane == coordinate.plane }
    }
    
    /**
     * Gets the centroid (center point) of this area.
     * @return The centroid coordinate or null if area is empty
     */
    fun getCentroid(): Tile? {
        val coords = getCoordinates()
        if (coords.isEmpty()) return null
        
        var totalX = 0
        var totalY = 0
        var totalZ = 0
        
        for (coord in coords) {
            totalX += coord.x
            totalY += coord.y
            totalZ += coord.plane
        }
        
        val size = coords.size
        return Tile.of(totalX / size, totalY / size, totalZ / size)
    }
    
    fun getCoordinate(): Tile? = getCentroid()
    
    // Abstract methods to be implemented by subclasses
    abstract fun toRectangular(): Rectangular
    abstract fun toPolygonal(): Polygonal
    abstract fun toCircular(): Circular
    abstract fun contains(locatable: Tile): Boolean
    abstract fun getCoordinates(): List<Tile>
    
    /**
     * Circular area implementation
     */
    class Circular(
        private val center: Tile,
        private val radius: Double
    ) : Area() {
        
        private var coordinatesCache: List<Tile>? = null
        
        override fun toRectangular(): Rectangular {
            val radiusInt = radius.toInt()
            return Rectangular(
                Tile.of(center.x - radiusInt, center.y - radiusInt, center.plane.toInt()),
                Tile.of(center.x + radiusInt, center.y + radiusInt, center.plane.toInt())
            )
        }
        
        override fun toPolygonal(): Polygonal {
            return toRectangular().toPolygonal()
        }
        
        override fun toCircular(): Circular = this
        override fun contains(locatable: Tile): Boolean {
            if (locatable.plane != center.plane) return false

            val dx = (locatable.x - center.x).toDouble()
            val dy = (locatable.y - center.y).toDouble()
            val distance = sqrt(dx * dx + dy * dy)

            return distance <= radius
        }


        
        fun derive(xOffset: Int, yOffset: Int, zOffset: Int): Circular {
            return Circular(
                Tile.of(center.x + xOffset, center.y + yOffset, center.plane + zOffset),
                radius
            )
        }
        
        override fun getCoordinates(): List<Tile> {
            if (coordinatesCache == null) {
                val coords = mutableListOf<Tile>()
                val centerX = center.x.toInt()
                val centerY = center.y.toInt()
                val plane = center.plane.toInt()
                
                // Generate coordinates within the circle
                for (angle in 0 until 360) {
                    val radians = Math.toRadians(angle.toDouble())
                    val x = (centerX + radius * cos(radians)).roundToInt()
                    val y = (centerY + radius * sin(radians)).roundToInt()
                    coords.add(Tile.of(x, y, plane))
                }
                
                coordinatesCache = coords.distinct()
            }
            
            return coordinatesCache!!
        }
        
        override fun getRandomCoordinate(): Tile {
            val angle = random(360)
            val radians = Math.toRadians(angle.toDouble())
            val x = (center.x + radius * cos(radians)).roundToInt()
            val y = (center.y + radius * sin(radians)).roundToInt()
            return Tile.of(x, y, center.plane.toInt())
        }
        
        fun getRadius(): Double = radius
        fun getCenter(): Tile = center
    }
    
    /**
     * Polygonal area implementation
     */
    class Polygonal(vararg coordinates: Tile) : Area() {
        
        private val polygon: Polygon
        private val plane: Int
        private var coordinatesCache: List<Tile>? = null
        
        init {
            require(coordinates.isNotEmpty()) { "Polygon must have at least one coordinate" }
            plane = coordinates[0].plane.toInt()
            polygon = Polygon()
            
            for (coord in coordinates) {
                polygon.addPoint(coord.x.toInt(), coord.y.toInt())
            }
        }
        
        override fun toRectangular(): Rectangular {
            val bounds = polygon.bounds
            return Rectangular(
                Tile.of(bounds.minX.toInt(), bounds.minY.toInt(), plane),
                Tile.of(bounds.maxX.toInt(), bounds.maxY.toInt(), plane)
            )
        }
        
        override fun toPolygonal(): Polygonal = this
        
        override fun toCircular(): Circular {
            return toRectangular().toCircular()
        }

        override fun contains(locatable: Tile): Boolean {
            if (locatable.plane.toInt() != plane) return false

            return polygon.contains(Point(locatable.x.toInt(), locatable.y.toInt()))
        }

        
        override fun getCoordinates(): List<Tile> {
            if (coordinatesCache == null || coordinatesCache!!.isEmpty()) {
                val coords = mutableListOf<Tile>()
                val bounds = polygon.bounds
                
                for (x in bounds.x until (bounds.x + bounds.width)) {
                    for (y in bounds.y until (bounds.y + bounds.height)) {
                        if (polygon.contains(x, y)) {
                            coords.add(Tile.of(x, y, plane))
                        }
                    }
                }
                
                coordinatesCache = coords
            }
            
            return coordinatesCache!!
        }

        override fun getRandomCoordinate(): Tile {
            val coords = getCoordinates()
            return coords[random(coords.size)]
        }
    }
    
    /**
     * Rectangular area implementation
     */
    class Rectangular(
        private var bottomLeft: Tile,
        private var topRight: Tile
    ) : Area() {
        
        init {
            // Ensure bottomLeft is actually bottom-left and topRight is top-right
            val minX = minOf(bottomLeft.x, topRight.x)
            val maxX = maxOf(bottomLeft.x, topRight.x)
            val minY = minOf(bottomLeft.y, topRight.y)
            val maxY = maxOf(bottomLeft.y, topRight.y)
            val maxZ = maxOf(bottomLeft.plane, topRight.plane)
            
            bottomLeft = Tile.of(minX.toInt(), minY.toInt(), maxZ.toInt())
            topRight = Tile.of(maxX.toInt(), maxY.toInt(), maxZ.toInt())
        }
        
        constructor(bottomLeft: Tile, width: Int, height: Int) : this(
            bottomLeft,
            Tile.of(bottomLeft.x + width, bottomLeft.y + height, bottomLeft.plane.toInt())
        )
        
        override fun toRectangular(): Rectangular = this
        
        override fun toPolygonal(): Polygonal {
            return Polygonal(
                getBottomLeft(),
                getBottomRight(),
                getTopRight(),
                getTopLeft()
            )
        }
        
        override fun toCircular(): Circular {
            val width = topRight.x - bottomLeft.x
            val height = topRight.y - bottomLeft.y
            val radius = minOf(width, height).toDouble()
            return Circular(getCoordinate()!!, radius)
        }

        override fun contains(locatable: Tile): Boolean {

            return bottomLeft.x <= locatable.x && locatable.x <= topRight.x &&
                    bottomLeft.y <= locatable.y && locatable.y <= topRight.y &&
                    bottomLeft.plane == locatable.plane
        }


        
        override fun getCoordinates(): List<Tile> {
            val coords = mutableListOf<Tile>()
            
            for (z in bottomLeft.plane..topRight.plane) {
                for (x in bottomLeft.x..topRight.x) {
                    for (y in bottomLeft.y..topRight.y) {
                        coords.add(Tile.of(x, y, z))
                    }
                }
            }
            
            return coords
        }
        
        override fun getRandomCoordinate(): Tile {
            val x = bottomLeft.x + random((topRight.x - bottomLeft.x + 1))
            val y = bottomLeft.y + random((topRight.y - bottomLeft.y + 1))
            return Tile.of(x, y, bottomLeft.plane.toInt())
        }
        
        fun derive(xOffset: Int, yOffset: Int, zOffset: Int): Rectangular {
            bottomLeft = Tile.of(bottomLeft.x - xOffset, bottomLeft.y - yOffset, bottomLeft.plane - zOffset)
            topRight = Tile.of(topRight.x + xOffset, topRight.y + yOffset, topRight.plane + zOffset)
            return this
        }
        
        fun getBottomRight(): Tile = Tile.of(topRight.x.toInt(), bottomLeft.y.toInt(), bottomLeft.plane.toInt())
        fun getTopLeft(): Tile = Tile.of(bottomLeft.x.toInt(), topRight.y.toInt(), topRight.plane.toInt())
        fun getBottomLeft(): Tile = bottomLeft
        fun getTopRight(): Tile = topRight
        
        override fun toString(): String {
            return "Bottom-Left: $bottomLeft | Top-Right: $topRight"
        }
    }

}
