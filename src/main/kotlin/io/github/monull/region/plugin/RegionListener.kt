package io.github.monull.region.plugin

import io.github.monull.region.Lands
import io.github.monull.region.canAccessLand
import io.github.monull.region.land.Land
import io.github.monull.region.nearestLand
import org.bukkit.Location
import org.bukkit.block.Container
import org.bukkit.block.Dispenser
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockCanBuildEvent
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerBedLeaveEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.bukkit.material.Directional
import java.util.*

class RegionListener : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.from.nearestLand != event.to.nearestLand) {
            if (!event.player.canAccessLand(event.from.nearestLand) || !event.player.canAccessLand(event.to.nearestLand)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onVehicleMove(event: VehicleMoveEvent) {
        val from = event.from.nearestLand
        val to = event.to.nearestLand
        if (from != to) {
            val vehicle = event.vehicle

            for (passenger in vehicle.passengers) {
                if (passenger is Player) {
                    if (!passenger.canAccessLand(from) || !passenger.canAccessLand(to)) {
                        passenger.eject()
                        passenger.teleport(event.from)
                        vehicle.teleport(event.from)
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onVehicleDestroy(event: VehicleDestroyEvent) {
        val vehicle = event.vehicle
        val area = vehicle.location.nearestLand
        val attacker = event.attacker

        if (attacker is Player) {
            if (!attacker.canAccessLand(area)) {
                event.isCancelled = true
            }
        }
    }

    private val checkTeleportCauses = EnumSet.of(
        PlayerTeleportEvent.TeleportCause.UNKNOWN,
        PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT,
        PlayerTeleportEvent.TeleportCause.ENDER_PEARL
    )


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        if (event.cause in checkTeleportCauses) {
            val player = event.player
            val from = event.from.nearestLand
            val to = event.to.nearestLand

            if (from != to) {
                if (!player.canAccessLand(from) || !player.canAccessLand(to)) event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        event.clickedBlock?.let { clicked ->
            val land = clicked.location.nearestLand

            if (!player.canAccessLand(land)) event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player
        val target = event.rightClicked

        if (!player.canAccessLand(target.location.nearestLand)) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerArmorStandManipulate(event: PlayerArmorStandManipulateEvent) {
        val player = event.player
        val target = event.rightClicked

        if (!player.canAccessLand(target.location.nearestLand)) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerBedLeave(event: PlayerBedLeaveEvent) {
        val player = event.player
        val bed = event.bed
        if (player.location.nearestLand != bed.location.nearestLand) {
            if (!player.canAccessLand(bed.location.nearestLand)) player.teleport(bed.location.add(0.5, 0.56250, 0.5))
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        var damager = event.damager

        if (damager is Player) {
            if (!damager.canAccessLand(entity.location.nearestLand)) event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        val projectile = event.entity
        val shooter = projectile.shooter

        if (shooter is Player) {
            if (!shooter.canAccessLand(projectile.location.nearestLand)) event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockCanBuild(event: BlockCanBuildEvent) {
        val player = event.player ?: return
        if (!player.canAccessLand(event.block.location.nearestLand)) event.isBuildable = false
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        if (!player.canAccessLand(event.block.location.nearestLand)) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        if (!player.canAccessLand(event.block.location.nearestLand)) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockIgnite(event: BlockIgniteEvent) {
        val land = event.block.location.nearestLand
        val player = event.player ?: return

        if (!player.canAccessLand(land)) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerBucketFill(event: PlayerBucketFillEvent) {
        val player = event.player
        if (!player.canAccessLand(event.blockClicked.location.nearestLand)) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerBucketEmpty(event: PlayerBucketEmptyEvent) {
        val player = event.player
        if (!player.canAccessLand(event.blockClicked.getRelative(event.blockFace).location.nearestLand)) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onHangingBreak(event: HangingBreakByEntityEvent) {
        var remover = event.remover ?: return

        if (remover is Player) {
            if (!remover.canAccessLand(event.entity.location.nearestLand)) event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onHangingPlace(event: HangingPlaceEvent) {
        val player = event.player ?: return

        if (!player.canAccessLand(event.block.location.nearestLand)) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val player = event.player
        if (!player.canAccessLand(event.itemDrop.location.nearestLand)) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerPickupItem(event: EntityPickupItemEvent) {
        val entity = event.entity
        if (entity is Player) {
            if (!entity.canAccessLand(event.item.location.nearestLand)) {
                event.isCancelled = true
                event.item.pickupDelay = 10
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPotionSplash(event: PotionSplashEvent) {
        for (affectedEntity in event.affectedEntities) {
            val land = event.potion.location.nearestLand

            if (affectedEntity is Player) {
                if (!affectedEntity.canAccessLand(land)) event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockSpread(event: BlockSpreadEvent) {
        val source = event.source.location.nearestLand
        val block = event.block.location.nearestLand

        if (source != block) {
            if (source.owner != "null" || block.owner != "null") event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockPistonExtend(event: BlockPistonExtendEvent) {
        val direction = event.direction
        val piston = event.block
        val head = piston.getRelative(direction)

        val pLand = piston.location.nearestLand
        val hLand = head.location.nearestLand

        if (pLand != hLand) {
            event.isCancelled = true
            return
        }
        for (block in event.blocks) {
            if (block.location.nearestLand != pLand || block.getRelative(direction).location.nearestLand != pLand) {
                event.isCancelled = true
                break
            }
        }

        for (block in event.blocks) {
            val bLand = block.location.nearestLand

            if (bLand != pLand) {
                event.isCancelled = true
                break
            }

            val toBlockLand = block.getRelative(direction).location.nearestLand

            if (toBlockLand != pLand) {
                event.isCancelled = true
                break
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockPistonRetract(event: BlockPistonRetractEvent) {
        val blocks = event.blocks

        if (blocks.isEmpty()) return

        val direction = event.direction
        val piston = event.block
        val pLand = piston.location.nearestLand

        for (block in event.blocks) {
            if (block.location.nearestLand != pLand || block.getRelative(direction).location.nearestLand != pLand) {
                event.isCancelled = true
                break
            }
        }

        for (block in event.blocks) {
            val bLand = block.location.nearestLand

            if (bLand != pLand) {
                event.isCancelled = true
                break
            }

            val toBlockLand = block.getRelative(direction).location.nearestLand

            if (toBlockLand != pLand) {
                event.isCancelled = true
                break
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockFromTo(event: BlockFromToEvent) {
        val from = event.block.location.nearestLand
        val to = event.toBlock.location.nearestLand

        if (from != to) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (event.entity.location.nearestLand.owner != "null") {
            event.isCancelled = true
        } else {
            event.blockList().removeIf { block ->
                block.location.nearestLand.owner != "null"
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockExplode(event: BlockExplodeEvent) {
        event.blockList().removeIf { it.location.nearestLand.owner != "null" }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockDispense(event: BlockDispenseEvent) {
        val block = event.block
        val state = block.state

        if (state is Dispenser) {
            val data = block.blockData
            if (data is Directional) {
                val from = block.location.nearestLand
                val to = block.getRelative(data.facing).location.nearestLand

                if (from != to) event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onInventoryMoveItem(event: InventoryMoveItemEvent) {
        val sourceHolder = event.source.holder
        val destionationHolder = event.destination.holder

        if (sourceHolder is Container && destionationHolder is Container) {
            val sourceLand = sourceHolder.block.location.nearestLand
            val destinationLand = destionationHolder.block.location.nearestLand

            if (sourceLand != destinationLand) event.isCancelled = true
        }
    }
}