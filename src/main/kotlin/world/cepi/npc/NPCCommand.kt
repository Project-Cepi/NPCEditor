package world.cepi.npc

import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.entity.Player
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.arguments.suggest
import world.cepi.npc.modification.SkinArguments

object NPCCommand : Command("npc") {

    init {

        // TODO meta

        val create = "create".literal()
        val delete = "delete".literal()
        val remove = "remove".literal()
        val summon = "summon".literal()
        val instances = "instances".literal()
        val skin = "skin".literal()
        val property = "property".literal()



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
                context[existingID].properties.keys.toList()
            }

        val remainingString = ArgumentType.StringArray("value")

        addSyntax(create, newID) {

            val player = sender as Player

            NPCManager.add(NPC(context.get(newID), player.instance!!, player.position))
        }

        addSyntax(skin, existingID, SkinArguments.usernameLiteral, SkinArguments.username) {
            context[existingID].skin = context[SkinArguments.username]
        }

        addSyntax(property, existingID, propertyCommand, propertyName, remainingString) {
            val properties = context[existingID].properties
            val name = context[propertyName]
            val value = context[remainingString].joinToString(" ")
            when (context[propertyCommand]!!) {
                "get" -> sender.sendMessage(
                    properties[name]
                        ?.let { "Property $name is set to $it" }
                        ?: "Property $name is not set to anything"
                )
                "set" -> {
                    properties[name]?.parse(sender, value)
                        ?: sender.sendMessage("Property $name is not found")
                }
            }
        }
    }


}