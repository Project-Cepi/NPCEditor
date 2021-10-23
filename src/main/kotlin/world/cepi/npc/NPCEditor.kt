package world.cepi.npc

import net.minestom.server.extensions.Extension
import world.cepi.npc.modification.SkinCommand

class NPCEditor : Extension() {

    override fun initialize() {

        NPCCommand.register()
        SkinCommand.register()

        eventNode.addChild(NPC.npcNode)

        logger.info("[NPCEditor] has been enabled!")
    }

    override fun terminate() {

        NPCCommand.unregister()
        SkinCommand.unregister()

        logger.info("[NPCEditor] has been disabled!")
    }

}