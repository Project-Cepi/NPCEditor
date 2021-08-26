package world.cepi.npc.properties

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.arguments.minecraft.ArgumentTime
import world.cepi.npc.NPC
import java.time.Duration

class RespawnTimeProperty(scope: CoroutineScope, npc: NPC, var duration: Duration) : Property<Duration>("respawnTime", npc) {
    init {
        npc.isAlive
            .filter { !it }
            .onEach {
                MinecraftServer.getSchedulerManager().buildTask {
                    npc.isAlive.value = true
                }.delay(duration).schedule()
            }
            .launchIn(scope)
    }

    override fun set(sender: CommandSender, value: String) {
        duration = ArgumentTime("").parse(value)
    }

    override fun get(): Duration = duration
}