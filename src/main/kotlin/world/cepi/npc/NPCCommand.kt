package world.cepi.npc

import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import net.minestom.server.entity.Player
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.arguments.suggest
import world.cepi.mob.mob.mobEgg
import world.cepi.npc.modification.SkinArguments

object NPCCommand : Command("npc") {

    init {

        // TODO meta

        val create = "create".literal()
        val delete = "delete".literal()
        val remove = "remove".literal()
        val summon = "summon".literal()
        val instances = "instances".literal()

        val newID = ArgumentType.Word("newID").map {
            if (NPCManager.contains(it))
                throw ArgumentSyntaxException("This ID already exists!", it, 1)

            it
        }

        val existingID = ArgumentType.Word("id").map {
            if (!NPCManager.contains(it))
                throw ArgumentSyntaxException("This ID does not exist!", it, 1)

            NPCManager[it]!!
        }.suggest {
            NPCManager.names.toList()
        }

        addSyntax(create, newID) {

            val player = sender as Player

            val mob = player.mobEgg ?: return@addSyntax

            NPCManager.add(NPC(
                context.get(newID),
                respawnPositions = mutableListOf(player.position),
                mob = mob,
                instance = player.instance!!
            ).also { it.attemptSpawn() })
        }

    }

}