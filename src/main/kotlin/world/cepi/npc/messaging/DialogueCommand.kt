package world.cepi.npc.messaging

import net.minestom.server.command.builder.arguments.ArgumentType
import world.cepi.kstom.adventure.asMini
import world.cepi.kstom.command.kommand.Kommand

object DialogueCommand : Kommand({

    val senderArgument = ArgumentType.String("sender")

    val receiver = ArgumentType.String("receiver")

    val message = ArgumentType.StringArray("message")
        .map { it.joinToString(" ").asMini() }

    syntax(senderArgument, receiver, message) {
        sender.sendMessage(Dialogue.create(context[senderArgument], context[receiver], context[message]))
    }

}, "dialogue")