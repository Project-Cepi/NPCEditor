package world.cepi.npc.modification

import net.minestom.server.entity.PlayerSkin
import world.cepi.kstom.command.kommand.Kommand
import java.util.*

object SkinCommand : Kommand({

    onlyPlayers()

    syntax(SkinArguments.selector, SkinArguments.userSkinInput) {
        val users = context[SkinArguments.userSkinInput].find(player)

        if (users.isEmpty()) return@syntax

        player.skin = PlayerSkin.fromUuid(
            context[SkinArguments.userSkinInput].find(player)[0].uuid.toString()
        )
    }

    syntax(SkinArguments.uuidLiteral, SkinArguments.uuid) {
        player.skin = PlayerSkin.fromUuid(
            context[SkinArguments.uuid].toString()
        )
    }

    syntax(SkinArguments.usernameLiteral, SkinArguments.username) {
        player.skin = context[SkinArguments.username]
    }

    syntax(SkinArguments.reset) {
        player.skin = PlayerSkin.fromUsername(player.username)
    }
}, "skin")