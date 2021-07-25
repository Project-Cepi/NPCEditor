package world.cepi.npc

import kotlinx.serialization.Transient
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.arguments.minecraft.ArgumentTime
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeBlockPosition
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.entity.fakeplayer.FakePlayer
import net.minestom.server.event.entity.EntityDeathEvent
import net.minestom.server.instance.Instance
import net.minestom.server.utils.Position
import net.minestom.server.utils.time.UpdateOption
import world.cepi.kstom.Manager
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

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
            field = value
            instances.forEach { it.fakePlayer.skin = value }
        }

    val positions: MutableList<Position> get() = instances.map { it.position }.toMutableList() // why mutable list?

    @Transient
    private val instances = mutableListOf<NPCInstance>()

    fun generateInstanceFromNPC(
        instance: Instance,
        position: Position
    ) {
        instances.add(NPCInstance(name, uuid, instance, skin, position))
    }

    init {
        positions.forEach {
            generateInstanceFromNPC(instance, it)
        }
    }

    val properties = mapOf(
        "spawnPosition" to SpawnPosition() ,
        "respawnDelay" to RespawnDelay()
    )

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

