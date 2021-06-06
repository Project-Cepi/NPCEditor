package world.cepi.npc

import net.minestom.server.extensions.Extension;
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister

class NPCEditor : Extension() {

    override fun initialize() {

        NPCCommand.register()

        logger.info("[NPCEditor] has been enabled!")
    }

    override fun terminate() {

        NPCCommand.unregister()

        logger.info("[NPCEditor] has been disabled!")
    }

}