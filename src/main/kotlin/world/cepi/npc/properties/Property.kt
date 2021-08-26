package world.cepi.npc.properties

import net.minestom.server.command.CommandSender
import world.cepi.npc.NPC

abstract class Property<T>(val name: String, val npc: NPC) {
    abstract fun set(sender: CommandSender, value: String)
    abstract fun get(): T
}