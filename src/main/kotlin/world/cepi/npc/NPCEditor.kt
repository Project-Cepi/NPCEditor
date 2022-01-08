package world.cepi.npc

import net.minestom.server.extensions.Extension
import world.cepi.kstom.util.log
import world.cepi.kstom.util.node
import world.cepi.npc.modification.SkinCommand

class NPCEditor : Extension() {

    override fun initialize(): LoadStatus {

        NPCCommand.register()
        SkinCommand.register()

        node.addChild(NPC.npcNode)

        log.info("[NPCEditor] has been enabled!")

        return LoadStatus.SUCCESS
    }

    override fun terminate() {

        NPCCommand.unregister()
        SkinCommand.unregister()

        log.info("[NPCEditor] has been disabled!")
    }

}