package world.cepi.npc

import kotlinx.serialization.Transient
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.entity.fakeplayer.FakePlayer
import net.minestom.server.entity.fakeplayer.FakePlayerOption
import net.minestom.server.instance.Instance
import net.minestom.server.utils.Position
import java.util.*

class NPC(
    val id: String,
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

    fun remove() {
        // TODO
    }

    fun onDeath() {
        Manager.scheduler.buildTask {
            attemptSpawn()
        }.delay(respawnInterval.toMillis(), TimeUnit.MILLISECOND).schedule()
    }

    sealed class NPCProperty {
        abstract fun parse(sender: CommandSender, value: String)
    }
    inner class SpawnPosition : NPCProperty() {
        var position: Position? = null
        override fun parse(sender: CommandSender, value: String) {
            position = ArgumentRelativeBlockPosition("")
                .parse(value) // abusing empty argument
                .from(sender.asPlayer())
                .toPosition()
            // creating an issue about extracting Parser from the Argument
            // and enforcing Argument to use the Parser would be helpful
        }
    }

    inner class RespawnDelay : NPCProperty() {
        var duration: Duration? = null
        override fun parse(sender: CommandSender, value: String) {
            duration = ArgumentTime("").parse(value).asDuration()
        }
        private fun UpdateOption.asDuration(): Duration = Duration.ofMillis(timeUnit.toMilliseconds(value))
    }
    inner class NPCInstance(
        val name: String,
        val uuid: UUID,
        val instance: Instance,
        val skin: PlayerSkin,
        val position: Position,
    ) {
        lateinit var fakePlayer: FakePlayer
        init {
            createFakePlayer(position)
        }
        val respawnDelay get() = (properties["respawnDelay"] as RespawnDelay).duration
        val spawnPosition get() = (properties["spawnPosition"] as SpawnPosition).position

        private fun createFakePlayer(position: Position) {
            FakePlayer.initPlayer(
                uuid,
                name
            ) {
                it.setInstance(instance, position)
                it.skin = skin
                fakePlayer = it
                respawnDelay?.run {
                    instance.addEventCallback(EntityDeathEvent::class.java) {
                        Manager.scheduler.timerExecutionService.schedule(
                            {
                                createFakePlayer(spawnPosition ?: this@NPCInstance.position)
                            },
                            toMillis(),
                            TimeUnit.MILLISECONDS
                        )
                    }
                }
            }
        }
    }
}