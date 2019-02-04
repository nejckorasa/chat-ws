package io.github.nejckorasa

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.Frame
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class ChatClient(private val session: DefaultWebSocketSession) {

    companion object {
        var lastId = AtomicInteger(0)
    }

    private val id = lastId.getAndIncrement()

    private val joinedTime = Date()
    private var lastReceiveTime: Date? = null
    private var lastSendTime: Date? = null

    private val sendCount = AtomicInteger(0)
    private val receiveCount = AtomicInteger(0)

    var name = "User[$id]"

    suspend fun error(errorMessage: String) {
        session.outgoing.send(Frame.Text("[Error] $errorMessage"))
    }

    suspend fun privateError(errorMessage: String) {
        session.outgoing.send(Frame.Text("[Only you][Error] $errorMessage"))
    }

    suspend fun privateSendOut(message: String) {
        session.outgoing.send(Frame.Text("[Only you] $message"))
    }

    suspend fun sendOut(message: String) {
        receiveCount.incrementAndGet()
        lastReceiveTime = Date()
        session.outgoing.send(Frame.Text(message))
    }

    fun markSend() {
        sendCount.incrementAndGet()
        lastSendTime = Date()
    }

    fun prettyPrint() = """


        Name: $name
        Id: $id
        joinedTime: $joinedTime

        sendCount: ${sendCount.get()}
        receiveCount: ${receiveCount.get()}

        lastSendTime: $lastSendTime
        lastReceiveTime: $lastReceiveTime

        """.trimIndent()
}