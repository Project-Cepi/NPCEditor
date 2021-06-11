package world.cepi.npc.modification

import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.literal

object SkinCommand : Command("skin") {

    init {
        val set = "set".literal()
        val reset = "reset".literal()

        val user = ArgumentType.Entity("user")
            .onlyPlayers(true)
            .singleEntity(true)

        addSyntax(set, user) { sender, args ->
            val player = sender as Player

            player.skin = PlayerSkin.fromUuid(
                args[user].find(player)[0].uuid.toString()
            )
        }

        addSyntax(reset) { sender ->
            val player = sender as Player

            player.skin = PlayerSkin.fromUsername(player.username)
        }
    }

}