package io.github.monull.region.merchant

import io.github.monull.region.Lands
import io.github.monull.region.land.Land
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.openFrame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class MerchantFrame {
    private val previousItem =
        ItemStack(Material.END_CRYSTAL).apply {
            itemMeta = itemMeta.apply {
                displayName(
                    text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.BOLD).content("↑").build()
                )
            }
        }
    private val nextItem =
        ItemStack(Material.END_CRYSTAL).apply {
            itemMeta = itemMeta.apply {
                displayName(
                    text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.BOLD).content("↓").build()
                )
            }
        }

    fun openMenuFrame(player: MerchantPlayer): InvFrame {
        return InvFX.frame(1, text("☛")) {
            slot(0, 0) {
                item = ItemStack(Material.GRASS_BLOCK).apply {
                    itemMeta = itemMeta.apply {
                        displayName(
                            text("시작 땅: ").color(NamedTextColor.GREEN)
                        )
                        lore(
                            listOf(
                                text().color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)
                                    .content("좌표: ").append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).content(player.initialLand.name.removeSuffix(".yml")).build()).build(),
                                text().color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                                    .content("땅값: ").append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).content(player.initialLand.price.toString())).build()
                            )
                        )
                    }
                }
            }

            slot(3, 0) {
                item = ItemStack(Material.RED_WOOL).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("땅 구매하기"))
                    }
                }

                onClick {
                    player.player.openFrame(openLandsFrame(player))
                }
            }

            slot(4, 0) {
                item = ItemStack(Material.YELLOW_WOOL).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("내 땅 보기"))
                    }
                }

                onClick {
                    player.player.openFrame(openMyLandsFrame(player))
                }
            }

            slot(5, 0) {
                item = ItemStack(Material.GREEN_WOOL).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("거래하기"))
                    }
                }

                onClick {
                    player.player.openFrame(openTradeFrame(player))
                }
            }
        }
    }

    fun openLandsFrame(player: MerchantPlayer): InvFrame {
        return InvFX.frame(5, text("부동산")) {
            for (y in 1..3) {
                slot(8, y) {
                    item = ItemStack(Material.YELLOW_WOOL)
                }
            }
            list(0, 0, 7, 4, false, { Lands.lands }) {
                transform {
                    ItemStack(Material.GRASS_BLOCK).apply {
                        itemMeta = itemMeta.apply {
                            displayName(text("(${it.locx.toInt()}, ${it.locz.toInt()})"))
                            lore(
                                listOf<Component>(
                                    text().color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                        .content("땅값: ").build().append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                            .content("${it.price}").build()),
                                    text().color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).content("주인: ").build()
                                        .append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).content(
                                            if (it.owner == "null") "없음" else it.owner
                                        ).build())
                                )
                            )
                            if (it.owner != "null") {
                                this.addEnchant(Enchantment.KNOCKBACK, 0, false)
                            }
                        }
                    }
                }

                onClickItem { x, y, (land, _), event ->
                    val item = event.currentItem!!
                    if (land.owner == "null" && player.player.inventory.contains(Material.DIAMOND, land.price)) {
                        player.player.inventory.remove(ItemStack(Material.DIAMOND, land.price))
                        land.owner = player.player.name
                        player.player.sendMessage(text("(${land.locx}, ${land.locz})에 있는 땅이 당신의 땅이 되었습니다."))
                        item.itemMeta = item.itemMeta.apply {
                            displayName(text("(${land.locx.toInt()}, ${land.locz.toInt()})"))
                            lore(
                                listOf<Component>(
                                    text().color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                        .content("땅값: ").build().append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                            .content("${land.price}").build()),
                                    text().color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).content("주인: ").build()
                                        .append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).content(
                                            if (land.owner == "null") "없음" else land.owner
                                        ).build())
                                )
                            )
                            if (land.owner != "null") {
                                this.addEnchant(Enchantment.KNOCKBACK, 0, false)
                            }
                        }
                    }
                }
            }.let { list ->
                slot(8, 0) {
                    item = previousItem
                    onClick {
                        list.index -= 8
                    }
                }
                slot(8, 4) {
                    item = nextItem
                    onClick {
                        list.index += 8
                    }
                }
            }
        }
    }

    fun openMyLandsFrame(player: MerchantPlayer): InvFrame {
        return InvFX.frame(3, text("내 땅")) {
            list(1, 0, 7, 0, false, { findMyLands(player) }) {
                transform {
                    ItemStack(Material.GRASS_BLOCK).apply {
                        itemMeta = itemMeta.apply {
                            displayName(text("(${it.locx.toInt()}, ${it.locz.toInt()})"))
                            lore(
                                listOf<Component>(
                                    text().color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                        .content("땅값: ").build().append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                            .content("${it.price}").build()),
                                    text().color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).content("주인: ").build()
                                        .append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).content(
                                            if (it.owner == "null") "없음" else it.owner
                                        ).build())
                                )
                            )
                        }
                    }
                }

                onClickItem { x, y, (land, _), event ->
                    player.player.openFrame(openMyLandSettings(player, land))
                }
            }
        }
    }

    fun openMyLandSettings(player: MerchantPlayer, land: Land): InvFrame {
        return InvFX.frame(1, text(land.name.removeSuffix(".yml"))) {
            slot(0, 0) {
                item = ItemStack(Material.COMPASS).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("내 땅으로 이동하기"))
                    }
                }

                onClick {
                    player.player.sendMessage(text("순간이동!"))
                    player.player.teleport(Location(Bukkit.getWorlds().first(), land.locx, Bukkit.getWorlds().first().getHighestBlockAt(land.locx.toInt(), land.locz.toInt()).location.y, land.locz))
                }
            }

            slot(1, 0) {
                item = ItemStack(Material.COMPASS).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("땅 팔기"))
                    }
                }

                onClick {
                    if (land == player.initialLand) {
                        player.player.sendMessage(text("초기 땅은 팔 수 없습니다!"))
                    } else {
                        land.owner = "null"
                        player.player.inventory.addItem(ItemStack(Material.DIAMOND, land.price))
                        player.player.sendMessage(text("(${land.locx}, ${land.locz})에 있는 땅을 팔았습니다."))
                        player.player.closeInventory()
                    }
                }
            }

            slot(2, 0) {
                item = ItemStack(Material.PLAYER_HEAD).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("화이트 리스트"))
                    }
                }

                onClick {
                    player.player.openFrame(openWhiteListFrame(player, land))
                }
            }
        }
    }

    fun openWhiteListFrame(player: MerchantPlayer, land: Land): InvFrame {
        return InvFX.frame(2, text("화이트 리스트")) {
            list(1, 0, 7, 0, false, { Bukkit.getOnlinePlayers().filter { it.name != player.player.name }}) {
                transform {
                    ItemStack(Material.PLAYER_HEAD).apply {
                        itemMeta = (itemMeta as SkullMeta).apply {
                            displayName(text(it.name))
                            this.setOwningPlayer(Bukkit.getOfflinePlayer(it.name))
                            lore(
                                listOf(
                                    if (land.whitelist.contains(it.name)) {
                                        text("친구").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                    } else text("남").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                                )
                            )
                        }
                    }
                }

                onClickItem { x, y, (player, _), event ->
                    val item = event.currentItem!!
                    if (land.whitelist.contains(player.name)) {
                        land.whitelist.remove(player.name)
                        item.itemMeta = (item.itemMeta as SkullMeta).apply {
                            displayName(text(player.name))
                            this.setOwningPlayer(Bukkit.getOfflinePlayer(player.name))
                            lore(
                                listOf(
                                    if (land.whitelist.contains(player.name)) {
                                        text("친구").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                    } else text("남").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                                )
                            )
                        }
                    } else {
                        land.whitelist.add(player.name)
                        item.itemMeta = (item.itemMeta as SkullMeta).apply {
                            displayName(text(player.name))
                            this.setOwningPlayer(Bukkit.getOfflinePlayer(player.name))
                            lore(
                                listOf(
                                    if (land.whitelist.contains(player.name)) {
                                        text("친구").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                    } else text("남").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun openTradeFrame(player: MerchantPlayer): InvFrame {
        val list = Bukkit.getOnlinePlayers().toList().filter { it != player.player }
        var num = 0
        return InvFX.frame(1, text("거래 요청하기")) {
            slot(1, 0) {
                item = ItemStack(Material.PLAYER_HEAD).apply {
                    itemMeta = (itemMeta as SkullMeta).apply {
                        owningPlayer = Bukkit.getOfflinePlayer(list.first().name)
                    }
                }

                onClick {
                    item = ItemStack(Material.PLAYER_HEAD).apply {
                        itemMeta = (itemMeta as SkullMeta).apply {
                            num += 1
                            if (num > list.size - 1) num = 0
                            owningPlayer = Bukkit.getOfflinePlayer(list[num].name)
                        }
                    }
                }
            }

            slot(7, 0) {
                item = ItemStack(Material.GREEN_WOOL).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("요청 보내기").color(NamedTextColor.GREEN))
                    }

                    onClick {
                        val p = list[num]
                        player.player.sendMessage(text("요청을 보냈습니다."))
                        p.sendMessage("${player.player.name}님이 거래를 요청하였습니다.")
                        p.sendMessage(text("거래 수락").color(NamedTextColor.GREEN).clickEvent(ClickEvent.callback {
                            trade(player, Lands.merchantPlayers.find { it.player == p }!!)
                        }))
                    }
                }
            }
        }
    }

    var pAccept = false
    var tAccept = false
    val ptradeList = arrayListOf<ItemStack>()
    val ttradeList = arrayListOf<ItemStack>()
    val pLandList = arrayListOf<Land>()
    val tLandList = arrayListOf<Land>()
    val pitemList = arrayListOf<ItemStack>()
    val titemList = arrayListOf<ItemStack>()
    var pLandFrame: InvFrame? = null
    var tLandFrame: InvFrame? = null
    val next = ItemStack(Material.NETHER_STAR).apply {
        itemMeta = itemMeta.apply {
            displayName(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                .decorate(TextDecoration.BOLD).content("→").build())
        }
    }
    val previous = ItemStack(Material.NETHER_STAR).apply {
        itemMeta = itemMeta.apply {
            displayName(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                .decorate(TextDecoration.BOLD).content("←").build())
        }
    }

    lateinit var pFrame: InvFrame
    lateinit var tFrame: InvFrame

    fun trade(player: MerchantPlayer, trader: MerchantPlayer) {
        pFrame = InvFX.frame(5, text("╚")) {
            for (i in 0..8) {
                if (i != 4) {
                    slot(i, 0) {
                        item = ItemStack(Material.WHITE_WOOL)
                    }
                } else {
                    slot (i, 0) {
                        item = ItemStack(Material.GREEN_WOOL).apply {
                            itemMeta = itemMeta.apply {
                                displayName(text("수락!"))
                            }
                        }
                        onClick {
                            pAccept = true
                            if (tAccept) {
                                tradeSuccess(player, trader, pitemList, titemList, pLandList, tLandList)
                                player.player.sendMessage(text("거래 성공!"))
                                trader.player.sendMessage(text("거래 성공!"))
                            } else {
                                trader.player.sendMessage(text("거래 수락[1/2]"))
                                player.player.sendMessage(text("거래 수락[1/2]"))
                            }
                        }
                    }
                }
                slot(i, 4) {
                    item = ItemStack(Material.WHITE_WOOL)
                }
                slot(i, 2) {
                    item = ItemStack(Material.WHITE_WOOL)
                }
            }
            slot(0, 1) {
                item = ItemStack(Material.WHITE_WOOL)
            }
            slot(0, 3) {
                item = ItemStack(Material.WHITE_WOOL)
            }
            slot(8, 1) {
                item = ItemStack(Material.WHITE_WOOL)
            }
            slot(8, 3) {
                item = ItemStack(Material.GLOBE_BANNER_PATTERN).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("집문서"))
                    }
                }

                onClick {
                    updatePLandFrame(player)
                    player.player.openFrame(pLandFrame!!)
                }
            }

            val list = list(2, 3, 6, 3, false, { ptradeList }) {
                onClickItem { x, y, item, event ->
                    if (pitemList.contains(item.first)) {
                        ptradeList.remove(event.currentItem)
                        pitemList.remove(event.currentItem)
                        player.player.inventory.addItem(event.currentItem!!)
                        event.currentItem = null
                        pAccept = false
                        tAccept = false
                    }
                }
            }

            slot(1, 3) {
                item = previous

                onClick {
                    list.index--
                }
            }
            slot(7, 3) {
                item = next

                onClick {
                    list.index++
                }
            }

            val list2 = list(2, 1, 6, 1, false, { ttradeList }) {

            }

            slot(1, 1) {
                item = previous

                onClick {
                    list2.index--
                }
            }
            slot(7, 1) {
                item = next

                onClick {
                    list2.index++
                }
            }

            onClickBottom { event ->
                if (event.currentItem != null) {
                    pitemList.add(event.currentItem!!)
                    ptradeList.add(event.currentItem!!)
                    event.currentItem = null
                    pAccept = false
                    tAccept = false
                }
                list.refresh()
            }

            Bukkit.getScheduler().runTaskTimer(Lands.plugin, Runnable { list2.refresh() }, 0L, 1L)
        }
        player.player.openFrame(
            pFrame
        )

        tFrame = InvFX.frame(5, text("╚")) {
            for (i in 0..8) {
                if (i != 4) {
                    slot(i, 0) {
                        item = ItemStack(Material.WHITE_WOOL)
                    }
                } else {
                    slot (i, 0) {
                        item = ItemStack(Material.GREEN_WOOL).apply {
                            itemMeta = itemMeta.apply {
                                displayName(text("수락!"))
                            }
                        }
                        onClick {
                            tAccept = true
                            if (pAccept) {
                                tradeSuccess(player, trader, pitemList, titemList, pLandList, tLandList)
                                player.player.sendMessage(text("거래 성공!"))
                                trader.player.sendMessage(text("거래 성공!"))
                            } else {
                                trader.player.sendMessage(text("거래 수락[1/2]"))
                                player.player.sendMessage(text("거래 수락[1/2]"))
                            }
                        }
                    }
                }
                slot(i, 4) {
                    item = ItemStack(Material.WHITE_WOOL)
                }
                slot(i, 2) {
                    item = ItemStack(Material.WHITE_WOOL)
                }
            }
            slot(0, 1) {
                item = ItemStack(Material.WHITE_WOOL)
            }
            slot(0, 3) {
                item = ItemStack(Material.WHITE_WOOL)
            }
            slot(8, 1) {
                item = ItemStack(Material.WHITE_WOOL)
            }
            slot(8, 3) {
                item = ItemStack(Material.GLOBE_BANNER_PATTERN).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("집문서"))
                    }
                }

                onClick {
                    updateTLandFrame(trader)
                    trader.player.openFrame(tLandFrame!!)
                }
            }

            val list = list(2, 3, 6, 3, false, { ttradeList }) {
                onClickItem { x, y, item, event ->
                    if (titemList.contains(item.first)) {
                        ttradeList.remove(event.currentItem)
                        titemList.remove(event.currentItem)
                        trader.player.inventory.addItem(event.currentItem!!)
                        event.currentItem = null
                        pAccept = false
                        tAccept = false
                    }
                }
            }

            slot(1, 3) {
                item = previous
                onClick { list.index-- }
            }

            slot(7, 3) {
                item = next
                onClick { list.index++ }
            }

            val list2 = list(2, 1, 6, 1, false, { ptradeList }) {

            }

            slot(1, 1) {
                item = previous
                onClick { list2.index-- }
            }

            slot(7, 1) {
                item = next
                onClick { list.index++ }
            }

            onClickBottom { event ->
                if (event.currentItem != null) {
                    titemList.add(event.currentItem!!)
                    ttradeList.add(event.currentItem!!)
                    event.currentItem = null
                    pAccept = false
                    tAccept = false
                }
                list.refresh()
            }

            Bukkit.getScheduler().runTaskTimer(Lands.plugin, Runnable { list2.refresh() }, 0L, 1L)
        }

        trader.player.openFrame(
            tFrame
        )
    }

    fun updatePLandFrame(player: MerchantPlayer) {
        pLandFrame = InvFX.frame(2, text("거래할 땅을 선택하시오.")) {
            val list = arrayListOf<Land>()
            list(0, 0, 7, 1, false, { Lands.lands.filter { it.owner == player.player.name && player.initialLand != it } }) {
                transform {
                    ItemStack(Material.GLOBE_BANNER_PATTERN).apply {
                        itemMeta = itemMeta.apply {
                            displayName(text("(${it.locx.toInt()}, ${it.locz.toInt()})"))
                            if (pLandList.contains(it)) {
                                lore(listOf(
                                    text("판매").color(NamedTextColor.GREEN)
                                ))
                                list.add(it)
                            } else {
                                lore(listOf(
                                    text("판매 안 함").color(NamedTextColor.RED)
                                ))
                            }
                        }
                    }
                }

                onClickItem { x, y, (land, _), event ->
                    if (list.contains(land)) {
                        list.remove(land)
                        event.currentItem?.itemMeta = event.currentItem!!.itemMeta.apply {
                            lore(listOf(
                                text("판매 안 함").color(NamedTextColor.RED)
                            ))
                        }
                    } else {
                        list.add(land)
                        event.currentItem?.itemMeta = event.currentItem!!.itemMeta.apply {
                            lore(listOf(
                                text("판매").color(NamedTextColor.GREEN)
                            ))
                        }
                    }
                }
            }

            slot(8, 0) {
                item = ItemStack(Material.GREEN_WOOL).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("돌아가기"))
                    }
                }

                onClick {
                    pLandList.clear()
                    ptradeList.clear()
                    pitemList.forEach {
                        ptradeList.add(it)
                    }
                    list.forEach {
                        ptradeList.add(ItemStack(Material.GLOBE_BANNER_PATTERN).apply {
                            itemMeta = itemMeta.apply {
                                displayName(text("(${it.locx.toInt()}, ${it.locz.toInt()})"))
                            }
                        })
                        pLandList.add(it)
                    }
                    player.player.openFrame(pFrame)
                }
            }
        }
    }

    fun updateTLandFrame(trader: MerchantPlayer) {
        tLandFrame = InvFX.frame(2, text("거래할 땅을 선택하시오.")) {
            val list = arrayListOf<Land>()
            list(0, 0, 7, 1, false, { Lands.lands.filter { it.owner == trader.player.name && trader.initialLand != it } }) {
                transform {
                    ItemStack(Material.GLOBE_BANNER_PATTERN).apply {
                        itemMeta = itemMeta.apply {
                            displayName(text("(${it.locx.toInt()}, ${it.locz.toInt()})"))
                            if (tLandList.contains(it)) {
                                lore(listOf(
                                    text("판매").color(NamedTextColor.GREEN)
                                ))
                                list.add(it)
                            } else {
                                lore(listOf(
                                    text("판매 안 함").color(NamedTextColor.RED)
                                ))
                            }
                        }
                    }
                }

                onClickItem { x, y, (land, _), event ->
                    if (list.contains(land)) {
                        list.remove(land)
                        event.currentItem?.itemMeta = event.currentItem!!.itemMeta.apply {
                            lore(listOf(
                                text("판매 안 함").color(NamedTextColor.RED)
                            ))
                        }
                    } else {
                        list.add(land)
                        event.currentItem?.itemMeta = event.currentItem!!.itemMeta.apply {
                            lore(listOf(
                                text("판매").color(NamedTextColor.GREEN)
                            ))
                        }
                    }
                }
            }

            slot(8, 0) {
                item = ItemStack(Material.GREEN_WOOL).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("돌아가기"))
                    }
                }

                onClick {
                    ttradeList.clear()
                    tLandList.clear()
                    titemList.forEach {
                        ttradeList.add(it)
                    }
                    list.forEach {
                        ttradeList.add(ItemStack(Material.GLOBE_BANNER_PATTERN).apply {
                            itemMeta = itemMeta.apply {
                                displayName(text("(${it.locx.toInt()}, ${it.locz.toInt()})"))
                            }
                        })
                        tLandList.add(it)
                    }
                    trader.player.openFrame(tFrame)
                }
            }
        }
    }

    fun tradeSuccess(player: MerchantPlayer, trader: MerchantPlayer, pItemList: List<ItemStack>, tItemList: List<ItemStack>, pLands: List<Land>, tLands: List<Land>) {
        player.player.closeInventory()
        trader.player.closeInventory()
        pItemList.forEach {
            trader.player.inventory.addItem(it)
        }
        tItemList.forEach {
            player.player.inventory.addItem(it)
        }
        pLands.forEach {
            it.owner = trader.player.name
        }
        tLands.forEach {
            it.owner = player.player.name
        }
    }

    fun findMyLands(player: MerchantPlayer): List<Land> {
        return Lands.lands.filter { it.owner == player.player.name }
    }
}