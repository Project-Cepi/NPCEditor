package world.cepi.npc

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minestom.server.extensions.Extension
import world.cepi.kstom.util.log
import world.cepi.kstom.util.node
import world.cepi.npc.NPCManager.internalNPCMap
import world.cepi.npc.modification.SkinCommand
import kotlin.io.path.*

class NPCEditor : Extension() {

    val npcFile by lazy { dataDirectory().resolve("npcs.json")}

    override fun initialize(): LoadStatus {

        if (npcFile.exists())
            internalNPCMap = Json.decodeFromString(npcFile.readText())
        else {
            npcFile.createDirectories()
            npcFile.createFile()
        }

        NPCCommand.register()
        SkinCommand.register()

        node.addChild(NPC.npcNode)

        log.info("[NPCEditor] has been enabled!")

        return LoadStatus.SUCCESS
    }

    override fun terminate() {

        npcFile.writeText(Json.encodeToString(internalNPCMap))

        NPCCommand.unregister()
        SkinCommand.unregister()

        log.info("[NPCEditor] has been disabled!")
    }

}