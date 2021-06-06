package world.cepi.npc

import net.minestom.server.extensions.Extension;

class NPCEditor : Extension() {

    override fun initialize() {
        logger.info("[NPCEditor] has been enabled!")
    }

    override fun terminate() {
        logger.info("[NPCEditor] has been disabled!")
    }

}