package world.cepi.npc

import kotlinx.serialization.Transient
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.entity.fakeplayer.FakePlayer
import net.minestom.server.instance.Instance
import net.minestom.server.utils.Position
import java.util.*

class NPC(
    val id: String,
    val instance: Instance,
    vararg positions: Position,
    val name: String = id,
    val uuid: UUID = UUID.randomUUID(),
    skin: PlayerSkin = PlayerSkin.fromUsername("MHF_Apple")!!
) {

    var skin: PlayerSkin = skin
        set(value) {
            instances.forEach { it.skin = value }
            field = value
        }

    val positions: MutableList<Position> = positions.toMutableList()

    @Transient
    private val instances = mutableListOf<FakePlayer>()

    fun generateInstanceFromNPC(
        instance: Instance,
        position: Position
    ) {
        FakePlayer.initPlayer(
            uuid,
            name
        ) {
            it.setInstance(instance, position)
            instances.add(it)
            it.skin = skin
        }
    }

    init {
        positions.forEach {
            generateInstanceFromNPC(instance, it)
        }
    }

}