package world.cepi.npc

import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityDeathEvent
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent
import net.minestom.server.instance.Instance
import net.minestom.server.tag.Tag
import net.minestom.server.utils.Position
import net.minestom.server.utils.time.TimeUnit
import world.cepi.kstom.Manager
import world.cepi.kstom.event.listenOnly
import world.cepi.mob.mob.Mob
import java.time.Duration
import java.util.*

class NPC(
    val id: String,
    var respawnInterval: Duration = Duration.of(5, TimeUnit.SECOND),
    val respawnPositions: MutableList<Position>,
    val instance: Instance,
    val mob: Mob
) {

    companion object {
        val npcNode = EventNode.type("npc", EventFilter.ENTITY)
    }

    val listenerNode = EventNode.type("npc-$id", EventFilter.ENTITY) { event, entity ->
        entity.getTag(Tag.String("npcID")) == id
    }

    init {

        npcNode.addChild(listenerNode)

        listenerNode.listenOnly<EntityDeathEvent> {
            isAlive = false
            onDeath()
        }

        listenerNode.listenOnly<RemoveEntityFromInstanceEvent> {
            isAlive = false
            onDeath()
        }
    }

    var isAlive = false

    fun attemptSpawn() {

        if (isAlive) return

        val creature = mob.generateMob() ?: return

        creature.setInstance(instance, respawnPositions.random())

        creature.setTag(Tag.String("npcID"), id)

        isAlive = true
    }

    fun onDeath() {
        Manager.scheduler.buildTask {
            attemptSpawn()
        }.delay(respawnInterval.toMillis(), TimeUnit.MILLISECOND).schedule()
    }
}