package world.cepi.npc.modification

import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import world.cepi.kstom.command.addSyntax
import java.util.*

object SkinCommand : Command("skin") {

    init {

        addSyntax(SkinArguments.selector, SkinArguments.userSkinInput) {
            val player = sender as Player

            val users = context[SkinArguments.userSkinInput].find(player)

            if (users.isEmpty()) return@addSyntax

            player.skin = PlayerSkin.fromUuid(
                context[SkinArguments.userSkinInput].find(player)[0].uuid.toString()
            )
        }

        addSyntax(SkinArguments.uuidLiteral, SkinArguments.uuid) {
            val player = sender as Player

            player.skin = PlayerSkin.fromUuid(
                context[SkinArguments.uuid].toString()
            )
        }

        addSyntax(SkinArguments.usernameLiteral, SkinArguments.username) {
            val player = sender as Player

            player.skin = context[SkinArguments.username]
        }

        addSyntax(SkinArguments.reset) {
            val player = sender as Player

            player.skin = PlayerSkin.fromUsername(player.username)
        }
    }

}