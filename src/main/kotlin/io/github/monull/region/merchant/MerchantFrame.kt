package io.github.monull.region.merchant

import io.github.monull.region.Lands
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.frame.InvFrame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object MerchantFrame {
    private val previousItem =
        ItemStack(Material.END_CRYSTAL).apply {
            itemMeta = itemMeta.apply {
                displayName(
                    text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.BOLD).content("←").build()
                )
            }
        }
    private val nextItem =
        ItemStack(Material.END_CRYSTAL).apply {
            itemMeta = itemMeta.apply {
                displayName(
                    text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.BOLD).content("→").build()
                )
            }
        }
    fun openFrame(): InvFrame {
        return InvFX.frame(3, Component.text("부동산")) {
            list(1, 0, 7, 0, false, { Lands.lands }) {
                transform {
                    ItemStack(Material.GRASS_BLOCK).apply {
                        itemMeta = itemMeta.apply {
                            displayName(Component.text("(${it.locx.toInt()}, ${it.locz.toInt()})"))
                        }
                    }
                }

                onClickItem { x, y, (land, item), event ->

                }
            }.let { list ->
                slot(0, 0) {
                    item = previousItem
                    onClick {
                        list.index--
                    }
                }
                slot(8, 0) {
                    item = nextItem
                    onClick {
                        list.index++
                    }
                }
            }
        }
    }
}