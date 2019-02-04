package io.github.nejckorasa

import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ActionsManager: KoinComponent {

    private val messageManager: MessageManager by inject()

    suspend fun performAction(client: ChatClient, text: String) {

        val action = ChatActions.values().find { text.startsWith(it.code) }
        when (action) {
            ChatActions.HELP -> {
                client.privateSendOut(ChatActions.values().joinToString("\n") { "${it.code} - ${it.description}" })
            }
            ChatActions.CHANGE_NAME -> {
                client.name = text.substringAfter(action.code)
                client.privateSendOut("Name changed to ${client.name}")
            }
            ChatActions.WHO_IS_HERE -> {
                val clients = ClientsManager.clients()
                client.privateSendOut("${clients.size} connected users: ${clients.joinToString(", ") { it.name }}")
            }
            ChatActions.MY_STATS -> {
                client.privateSendOut(client.prettyPrint())
            }
            ChatActions.SHARE_TO -> {
                val actionMessage = text.substringAfter(action.code)
                val clientName = ClientsManager.clients().map { it.name }.firstOrNull { actionMessage.startsWith(it) }

                when (clientName) {
                    null -> client.privateError("Cannot find user $clientName")
                    else -> {
                        val message = actionMessage.substringAfter(clientName)
                        messageManager.shareMessage(client, message, clientName)
                    }
                }
            }
            else -> client.privateError("Unknown action $text")
        }
    }
}

enum class ChatActions(val code: String, val description: String) {
    HELP("/help", "get help"),
    CHANGE_NAME("/chname ", "change name (example: /chname <new_user_name>"),
    WHO_IS_HERE("/whohere", "list connected users"),
    SHARE_TO("/for ", "share message for specific user (example: /for user1 my message to share)"),
    MY_STATS("/mystats", "see your stats")
}