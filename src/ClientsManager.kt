package io.github.nejckorasa

import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.*

class ClientsManager: KoinComponent {

    private val messageManager: MessageManager by inject()

    companion object {

        private val clients: MutableSet<ChatClient> = Collections.synchronizedSet(LinkedHashSet<ChatClient>())

        fun clients(): List<ChatClient> = clients.toList()
    }

    suspend fun addClient(client: ChatClient) {
        client.privateSendOut("Hello ${client.name}! Want help? type /help")
        messageManager.shareMessage("${client.name} joined chat")
        clients += client
    }

    suspend fun removeClient(client: ChatClient) {
        messageManager.shareMessage("${client.name} left chat")
        clients -= client
    }
}