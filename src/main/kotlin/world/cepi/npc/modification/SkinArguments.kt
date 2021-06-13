package world.cepi.npc.modification

import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.entity.PlayerSkin
import world.cepi.kstom.command.arguments.literal
import java.lang.Exception
import java.util.*

object SkinArguments {

    val uuidLiteral = "uuid".literal()
    val usernameLiteral = "username".literal()
    val selector = "selector".literal()
    val reset = "reset".literal()

    val userSkinInput = ArgumentType.Entity("user")
        .onlyPlayers(true)
        .singleEntity(true)

    val uuid = ArgumentType.Word("uuidInput").map {
        try {
            UUID.fromString(it)
        } catch (e: Exception) {
            throw ArgumentSyntaxException("Invalid uuid", it, 1)
        }
    }

    val username = ArgumentType.Word("usernameInput").map {
        try {
            PlayerSkin.fromUsername(it) ?: throw ArgumentSyntaxException("Skin not found", it, 1)
        } catch (e: Exception) {
            throw ArgumentSyntaxException("Skin not found", it, 1)
        }
    }

}