package world.cepi.npc

import net.minestom.server.extensions.Extension;
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import world.cepi.npc.modification.SkinCommand

class NPCEditor : Extension() {

    override fun initialize() {

        NPCCommand.register()
        SkinCommand.register()

        logger.info("[NPCEditor] has been enabled!")
    }

    override fun terminate() {

        NPCCommand.unregister()
        SkinCommand.unregister()

        logger.info("[NPCEditor] has been disabled!")
    }

}