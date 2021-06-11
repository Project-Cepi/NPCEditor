package world.cepi.npc

import net.minestom.server.entity.fakeplayer.FakePlayer
import net.minestom.server.instance.Instance
import net.minestom.server.utils.Position
import java.util.*

class NPC(
    val id: String,
    val instance: Instance,
    vararg positions: Position,
    val name: String = id,
    val uuid: UUID = UUID.randomUUID()
) {

    val positions: MutableList<Position> = positions.toMutableList()

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
        }
    }

    init {
        positions.forEach {
            generateInstanceFromNPC(instance, it)
        }
    }

}