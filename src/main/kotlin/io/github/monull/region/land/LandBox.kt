package io.github.monull.region.land

class LandBox(val minX: Double, val minZ: Double, val maxX: Double, val maxZ: Double) {
    fun contain(x: Double, z: Double): Boolean {
        return x in minX..maxX && z in minZ..maxZ
    }
}