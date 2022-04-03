package world.cepi.npc.properties

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeBlockPosition
import net.minestom.server.coordinate.Pos
import net.minestom.server.tag.Tag
import world.cepi.kstom.serializer.PositionSerializer
import world.cepi.npc.NPC

class RespawnPositionProperty(
    scope: CoroutineScope,
    npc: NPC,
    val respawnPositions: MutableList<Pos>
) : Property<List<Pos>>("respawnPosition", npc, ) {
    init {
        npc.isAlive
            .filter { it }
            .onEach {
                npc.currentEntity = npc.mob
                    .generateMob()
                    ?.apply {
                        setInstance(npc.instance, respawnPositions.random())
                        setTag(Tag.String("npcID"), npc.id)
                    }
            }
            .launchIn(scope)
    }
    override fun set(sender: CommandSender, value: String) {
        respawnPositions.clear()
        respawnPositions += Argument.parse(ArgumentRelativeBlockPosition(value))
            .fromSender(sender)
            .asPosition()
    }

    override fun get(): List<Pos> = respawnPositions
}