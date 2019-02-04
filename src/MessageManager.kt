package io.github.nejckorasa

class MessageManager {

    suspend fun shareMessage(message: String) {

        ClientsManager.clients()
            .forEach { it.sendOut(message) }
    }

    suspend fun shareMessage(fromClient: ChatClient, message: String) {

        ClientsManager.clients()
            .forEach { it.sendOut("${fromClient.name} says: $message") }

        fromClient.markSend()
    }

    suspend fun shareMessage(fromClient: ChatClient, message: String, clientName: String) {

        ClientsManager.clients()
            .filter { clientName == it.name || fromClient.name == it.name }
            .forEach { it.sendOut("${fromClient.name} says to $clientName: $message") }

        fromClient.markSend()
    }

}