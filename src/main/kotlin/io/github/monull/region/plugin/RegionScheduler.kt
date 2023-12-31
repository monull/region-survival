package io.github.monull.region.plugin

import io.github.monull.region.Lands
import java.util.Random

class RegionScheduler : Runnable {
    var ticks = 0
    override fun run() {
        ticks++

        if (ticks == 20 * 60 * 5) {
            Lands.lands.forEach {
                val random = Random().nextInt(11) - 5
                val ex = it.price + random
                if (ex > 0) it.price = ex
            }
            ticks = 0
        }
    }

}