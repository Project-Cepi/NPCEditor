package world.cepi.npc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityDeathEvent
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent
import net.minestom.server.instance.Instance
import net.minestom.server.tag.Tag
import world.cepi.kstom.event.listenOnly
import world.cepi.kstom.serializer.DurationSerializer
import world.cepi.kstom.serializer.PositionSerializer
import world.cepi.mob.mob.Mob
import world.cepi.npc.properties.RespawnPositionProperty
import world.cepi.npc.properties.RespawnTimeProperty
import java.time.Duration
import java.util.*

@Serializable
class NPC(
    val id: String,
    @Transient
    val instance: Instance = MinecraftServer.getInstanceManager().instances.first(),
    val respawnPositions: MutableList<@Serializable(with = PositionSerializer::class) Pos>,
    val duration: @Serializable(with = DurationSerializer::class) Duration = Duration.ZERO,
    val mob: Mob
) {

    companion object {
        val npcNode = EventNode.type("npc", EventFilter.ENTITY)
    }

    @Transient
    val listenerNode = EventNode.type("npc-$id", EventFilter.ENTITY) { event, entity ->
        entity.getTag(Tag.String("npcID")) == id
    }

    @Transient
    val propertyScope = CoroutineScope(Dispatchers.IO)

    @Transient
    val properties = listOf(
        RespawnPositionProperty(propertyScope, this, respawnPositions),
        RespawnTimeProperty(propertyScope, this, duration)
    )

    @Transient
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