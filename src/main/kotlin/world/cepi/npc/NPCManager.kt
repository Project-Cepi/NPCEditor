package world.cepi.npc

object NPCManager {

    private val internalNPCMap = HashMap<String, NPC>()

    val names = internalNPCMap.keys

    fun contains(name: String) = internalNPCMap.contains(name)


}