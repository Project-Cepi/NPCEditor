package world.cepi.npc

import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.arguments.suggest
import world.cepi.kstom.command.kommand.Kommand
import world.cepi.mob.mob.mobEgg
import world.cepi.npc.modification.SkinArguments

object NPCCommand : Kommand({

    val create by literal
    val delete by literal
    val remove by literal
    val summon by literal
    val instances by literal
    val property by literal



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

    val propertyCommand = ArgumentType
        .Word("propertyCommand")
        .from("get", "set")

    val propertyName = ArgumentType.Word("name")
        .suggest {
            context[existingID].properties.map { it.name }
        }

    val remainingString = ArgumentType.StringArray("value")

    syntax(create, newID).onlyPlayers {
        val mob = player.mobEgg ?: return@onlyPlayers

        NPCManager.add(NPC(
            context.get(newID),
            respawnPositions = mutableListOf(player.position),
            mob = mob,
            instance = player.instance!!
        ).also { it.attemptSpawn() })
    }

    syntax(delete, existingID) {
        NPCManager.remove(existingID.id)
    }
    syntax(property, existingID, propertyCommand, propertyName, remainingString) {
        val properties = context[existingID].properties
        val name = context[propertyName]
        val value = context[remainingString].joinToString(" ")
        when (context[propertyCommand]!!) {
            "get" -> sender.sendMessage(
                properties.firstOrNull { it.name == name }
                    ?.let { "Property $name is set to $it" }
                    ?: "Property $name is not set to anything"
            )
            "set" -> {
                properties.firstOrNull { it.name == name }
                    ?.set(sender, value)
                    ?: sender.sendMessage("Property $name is not found")
            }
        }
    }


}, "npc")