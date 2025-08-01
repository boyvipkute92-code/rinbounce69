/*
 * RinBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/rattermc/rinbounce69
 */
package net.ccbluex.liquidbounce.features.command

import net.ccbluex.liquidbounce.features.command.commands.*
import net.ccbluex.liquidbounce.features.command.shortcuts.Shortcut
import net.ccbluex.liquidbounce.features.command.shortcuts.ShortcutParser
import net.ccbluex.liquidbounce.features.command.special.*
import net.ccbluex.liquidbounce.file.FileManager.saveConfig
import net.ccbluex.liquidbounce.file.FileManager.shortcutsConfig
import net.ccbluex.liquidbounce.utils.client.chat

object CommandManager {
    val commands = mutableListOf<Command>()
    var latestAutoComplete = emptyArray<String>()

    var prefix = "."

    /**
     * Register all default commands
     */
    fun registerCommands() {
        commands.clear()

        registerCommand(AutoDisableCommand)
        registerCommand(BindCommand)
        registerCommand(VClipCommand)
        registerCommand(HClipCommand)
        registerCommand(HelpCommand)
        registerCommand(SayCommand)
        registerCommand(FriendCommand)
        registerCommand(SettingsCommand)
        registerCommand(PacketDebuggerCommand)
        registerCommand(LocalSettingsCommand)
        registerCommand(LocalThemesCommand)
        registerCommand(ServerInfoCommand)
        registerCommand(ToggleCommand)
        registerCommand(HurtCommand)
        registerCommand(GiveCommand)
        registerCommand(UsernameCommand)
        registerCommand(TargetCommand)
        registerCommand(TacoCommand)
        registerCommand(BindsCommand)
        registerCommand(HoloStandCommand)
        registerCommand(PanicCommand)
        registerCommand(PingCommand)
        registerCommand(RenameCommand)
        registerCommand(EnchantCommand)
        registerCommand(ReloadCommand)
        registerCommand(ScriptManagerCommand)
        registerCommand(RemoteViewCommand)
        registerCommand(PrefixCommand)
        registerCommand(ShortcutCommand)
        registerCommand(XrayCommand)
        registerCommand(LiquidChatCommand)
        registerCommand(PrivateChatCommand)
        registerCommand(ChatTokenCommand)
        registerCommand(ChatAdminCommand)
        registerCommand(TeleportCommand)
        registerCommand(AICommand)
        registerCommand(MapleAICommand)
    }

    /**
     * Execute command by given [input]
     */
    fun executeCommands(input: String) {
        if (!input.startsWith(prefix)) {
            return
        }

        val args = input.removePrefix(prefix).split(' ').toTypedArray()

        for (command in commands) {
            if (args[0].equals(command.command, ignoreCase = true)) {
                command.execute(args)
                return
            }

            for (alias in command.alias) {
                if (!args[0].equals(alias, ignoreCase = true))
                    continue

                command.execute(args)
                return
            }
        }

        chat("§cCommand not found. Type ${prefix}help to view all commands.")
    }

    /**
     * Updates the [latestAutoComplete] array based on the provided [input].
     *
     * @param input text that should be used to check for auto completions.
     * @author NurMarvin
     */
    fun autoComplete(input: String): Boolean {
        latestAutoComplete = getCompletions(input) ?: emptyArray()
        return input.startsWith(prefix) && latestAutoComplete.isNotEmpty()
    }

    /**
     * Returns the auto completions for [input].
     *
     * @param input text that should be used to check for auto completions.
     * @author NurMarvin
     */
    private fun getCompletions(input: String): Array<String>? {
        if (!input.startsWith(prefix)) {
            return null
        }

        val rawInput = input.removePrefix(prefix)

        val args = rawInput.split(' ').toTypedArray()

        return if (args.size > 1) {
            val command = getCommand(args[0])
            val tabCompletions = command?.tabComplete(args.copyOfRange(1, args.size))

            tabCompletions?.toTypedArray()
        } else {
            commands.mapNotNull { command ->
                val alias = when {
                    command.command.startsWith(rawInput, true) -> command.command
                    else -> command.alias.firstOrNull { alias -> alias.startsWith(rawInput, true) }
                } ?: return@mapNotNull null

                prefix + alias
            }.toTypedArray()
        }
    }

    /**
     * Get command instance by given [name]
     */
    fun getCommand(name: String) = commands.find {
        it.command.equals(name, ignoreCase = true) || it.alias.any { alias -> alias.equals(name, true) }
    }

    /**
     * Register [command] by just adding it to the commands registry
     */
    fun registerCommand(command: Command) = commands.add(command)

    fun registerShortcut(name: String, script: String) {
        if (getCommand(name) == null) {
            registerCommand(Shortcut(name, ShortcutParser.parse(script).map {
                val command = getCommand(it[0]) ?: throw IllegalArgumentException("Command ${it[0]} not found!")

                command to it.toTypedArray()
            }))

            saveConfig(shortcutsConfig)
        } else {
            throw IllegalArgumentException("Command already exists!")
        }
    }

    fun unregisterShortcut(name: String): Boolean {
        val removed = commands.removeIf {
            it is Shortcut && it.command.equals(name, ignoreCase = true)
        }

        saveConfig(shortcutsConfig)

        return removed
    }

    /**
     * Unregister [command] by just removing it from the commands registry
     */
    fun unregisterCommand(command: Command?) = commands.remove(command)
}
