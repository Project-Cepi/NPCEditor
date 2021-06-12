package world.cepi.npc.modification

import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.literal
import java.lang.Exception
import java.util.*

object SkinCommand : Command("skin") {

    init {
        val uuidLiteral = "uuid".literal()
        val usernameLiteral = "username".literal()
        val selector = "selector".literal()
        val reset = "reset".literal()

        val user = ArgumentType.Entity("user")
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

        addSyntax(selector, user) { sender, args ->
            val player = sender as Player

            val users = args[user].find(player)

            if (users.isEmpty()) return@addSyntax

            player.skin = PlayerSkin.fromUuid(
                args[user].find(player)[0].uuid.toString()
            )
        }

        addSyntax(uuidLiteral, uuid) { sender, args ->
            val player = sender as Player

            player.skin = PlayerSkin.fromUuid(
                args[uuid].toString()
            )
        }

        addSyntax(usernameLiteral, username) { sender, args ->
            val player = sender as Player

            player.skin = args[username]
        }

        addSyntax(reset) { sender ->
            val player = sender as Player

            player.skin = PlayerSkin.fromUsername(player.username)
        }
    }

}