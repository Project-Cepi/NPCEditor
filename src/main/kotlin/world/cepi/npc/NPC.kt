package world.cepi.npc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Transient
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.entity.fakeplayer.FakePlayer
import net.minestom.server.entity.fakeplayer.FakePlayerOption
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityDeathEvent
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent
import net.minestom.server.instance.Instance
import net.minestom.server.tag.Tag
import world.cepi.kstom.event.listenOnly
import world.cepi.mob.mob.Mob
import world.cepi.npc.properties.RespawnPositionProperty
import world.cepi.npc.properties.RespawnTimeProperty
import java.time.Duration
import java.util.*

class NPC(
    val id: String,
    val instance: Instance,
    respawnPositions: MutableList<Pos>,
    duration: Duration = Duration.ZERO,
    val mob: Mob
) {

    companion object {
        val npcNode = EventNode.type("npc", EventFilter.ENTITY)
    }

    val listenerNode = EventNode.type("npc-$id", EventFilter.ENTITY) { event, entity ->
        entity.getTag(Tag.String("npcID")) == id
    }
    val propertyScope = CoroutineScope(Dispatchers.IO)
    val properties = listOf(
        RespawnPositionProperty(propertyScope, this, respawnPositions),
        RespawnTimeProperty(propertyScope, this, duration)
    )
    var currentEntity: EntityCreature? = null

    init {

        npcNode.addChild(listenerNode)

        listenerNode.listenOnly<EntityDeathEvent> {
            isAlive.value = false
        }

        listenerNode.listenOnly<RemoveEntityFromInstanceEvent> {
            isAlive.value = false
        }
    }

    var isAlive = MutableStateFlow(false)

    fun attemptSpawn() {
        if (isAlive.value) return
        isAlive.value = true
    }

    fun remove() {
        propertyScope.cancel()
        currentEntity?.remove()
    }
}