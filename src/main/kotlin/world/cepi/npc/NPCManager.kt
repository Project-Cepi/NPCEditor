package world.cepi.npc

object NPCManager {

    internal var internalNPCMap = HashMap<String, NPC>()

    val names = internalNPCMap.keys

    fun contains(id: String) = internalNPCMap.contains(id)

    fun add(npc: NPC) {
        if (contains(npc.id)) return

        internalNPCMap[npc.id] = npc
    }

    operator fun get(id: String) = internalNPCMap[id]

    fun remove(id: String) {
        internalNPCMap.remove(id)
    }


}