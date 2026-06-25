package com.undercut.util

import com.undercut.game.Tile

object MapUtils {

    private interface StructureEncoder {
        fun encode(x: Int, y: Int, plane: Int): Int
    }

    private interface StructureDecoder {
        fun decode(id: Int): IntArray
    }

    enum class Structure(
        val child: Structure?,
        private val width: Int,
        private val height: Int,
        private val encoder: StructureEncoder,
        private val decoder: StructureDecoder
    ) {

        TILE(null, 1, 1, object : StructureEncoder {
            override fun encode(x: Int, y: Int, plane: Int): Int {
                return y or (x shl 14) or (plane shl 28)
            }
        }, object : StructureDecoder {
            override fun decode(id: Int): IntArray {
                return intArrayOf(id shr 14 and 16383, id and 16383, id shr 28 and 3)
            }
        }),

        CHUNK(TILE, 8, 8, object : StructureEncoder {
            override fun encode(x: Int, y: Int, plane: Int): Int {
                return (x shl 11) or y or (plane shl 22)
            }
        }, object : StructureDecoder {
            override fun decode(id: Int): IntArray {
                return intArrayOf(id shr 11 and 2047, id and 2047, id shr 22 and 3)
            }
        }),

        REGION(CHUNK, 8, 8, object : StructureEncoder {
            override fun encode(x: Int, y: Int, plane: Int): Int {
                return (x shl 8) or y or (plane shl 16)
            }
        }, object : StructureDecoder {
            override fun decode(id: Int): IntArray {
                return intArrayOf(id shr 8 and 255, id and 255, id shr 24 and 3)
            }
        });

        fun getWidth(): Int {
            var x = width
            var nextChild = child
            while (nextChild != null) {
                x *= nextChild.width
                nextChild = nextChild.child
            }
            return x
        }

        fun getHeight(): Int {
            var y = height
            var nextChild = child
            while (nextChild != null) {
                y *= nextChild.height
                nextChild = nextChild.child
            }
            return y
        }

        fun encode(x: Int, y: Int, plane: Int = 0): Int {
            return encoder.encode(x, y, plane)
        }

        fun decode(id: Int): IntArray {
            return decoder.decode(id)
        }
    }

    data class Area(
        val structure: Structure,
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int
    ) {

        fun getMapX(): Int = x * structure.getWidth()

        fun getMapY(): Int = y * structure.getHeight()

        fun getMapWidth(): Int = width * structure.getWidth()

        fun getMapHeight(): Int = height * structure.getHeight()

        fun getRandomTile(): Tile {
            return Tile.of(x + random(width), y + random(height), 0)
        }

        fun getRandomTile(fromCenter: Int): Tile {
            return Tile.of(
                x + (width / 2 + random(-fromCenter, fromCenter + 1)),
                y + (height / 2 + random(-fromCenter, fromCenter + 1)),
                0
            )
        }

        fun within(tile: Tile): Boolean {
            return tile.getX() in getMapX()..(getMapX() + getMapWidth()) &&
                    tile.getY() in getMapY()..(getMapY() + getMapHeight())
        }

        override fun hashCode(): Int {
            return structure.encode(x, y)
        }

        override fun toString(): String {
            return "Structure: $structure, x: $x, y: $y, width: $width, height: $height"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false
            other as Area
            if (structure != other.structure) return false
            if (x != other.x) return false
            if (y != other.y) return false
            if (width != other.width) return false
            if (height != other.height) return false
            return true
        }
    }

    fun getArea(min: Tile, max: Tile): Area {
        return getArea(Structure.TILE, min.getX(), min.getY(), max.getX(), max.getY())
    }

    fun getArea(minX: Int, minY: Int, maxX: Int, maxY: Int): Area {
        return getArea(Structure.TILE, minX, minY, maxX, maxY)
    }

    fun getArea(structure: Structure, minX: Int, minY: Int, maxX: Int, maxY: Int): Area {
        return Area(structure, minX, minY, maxX - minX, maxY - minY)
    }

    fun within(area: Area, tile: Tile): Boolean {
        return area.within(tile)
    }

    fun convert(to: Structure, area: Area): Area {
        val x = area.getMapX() / to.getWidth()
        val y = area.getMapY() / to.getHeight()
        val width = area.getMapWidth() / to.getWidth()
        val height = area.getMapHeight() / to.getHeight()
        return Area(to, x, y, width, height)
    }

    fun convert(from: Structure, to: Structure, vararg xy: Int): IntArray {
        return intArrayOf(
            xy[0] * from.getWidth() / to.getWidth(),
            xy[1] * from.getHeight() / to.getHeight()
        )
    }

    fun encode(structure: Structure, vararg xyp: Int): Int {
        return structure.encode(xyp[0], xyp[1], if (xyp.size == 3) xyp[2] else 0)
    }

    fun decode(structure: Structure, id: Int): IntArray {
        return structure.decode(id)
    }

    fun chunkToRegionId(chunkId: Int): Int {
        val tile = Structure.CHUNK.decode(chunkId)
        return Tile.of(tile[0] shl 3, tile[1] shl 3, 0).regionId
    }
}