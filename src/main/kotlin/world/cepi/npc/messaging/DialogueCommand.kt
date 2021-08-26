package world.cepi.npc.messaging

import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import world.cepi.kstom.adventure.asMini
import world.cepi.kstom.command.addSyntax

object DialogueCommand : Command("dialogue") {

    init {
        val senderArgument = ArgumentType.String("sender")

        val receiver = ArgumentType.String("receiver")

        val message = ArgumentType.StringArray("message").map { it.joinToString(" ").asMini() }

        addSyntax(senderArgument, receiver, message) {
            sender.sendMessage(Dialogue.create(context[senderArgument], context[receiver], context[message]))
        }
    }

}