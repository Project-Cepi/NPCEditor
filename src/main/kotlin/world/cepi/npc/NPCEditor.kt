package world.cepi.npc

import net.minestom.server.extensions.Extension;
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import world.cepi.npc.messaging.DialogueCommand
import world.cepi.npc.modification.SkinCommand

class NPCEditor : Extension() {

    override fun initialize() {

        NPCCommand.register()
        SkinCommand.register()
        DialogueCommand.register()

        eventNode.addChild(NPC.npcNode)

        logger.info("[NPCEditor] has been enabled!")
    }

    override fun terminate() {

        NPCCommand.unregister()
        SkinCommand.unregister()
        DialogueCommand.unregister()

        logger.info("[NPCEditor] has been disabled!")
    }

}