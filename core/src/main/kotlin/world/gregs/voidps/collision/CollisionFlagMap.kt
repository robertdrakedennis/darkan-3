package world.gregs.voidps.collision

/**
 * rsmod routefinder-compatible name for Darkan's existing per-tile clip-flag grid.
 *
 * The underlying [CollisionMap] already stores the NXT/rsmod bit table, returns `-1`
 * for absent zones, and exposes `add`/`remove` helpers for routefinder callers.
 */
typealias CollisionFlagMap = CollisionMap
