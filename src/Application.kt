package io.github.nejckorasa

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.ktor.websocket.webSocket
import org.koin.dsl.module.module
import org.koin.ktor.ext.inject
import org.koin.standalone.StandAloneContext.startKoin
import java.time.Duration

// Koin module
val chatModule = module {
    single { ActionsManager() }
    single(createOnStart = true) { MessageManager() }
    single(createOnStart = true) { ClientsManager() }
}

fun main(args: Array<String>) {
    startKoin(listOf(chatModule))
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("UNUSED_PARAMETER")
@KtorExperimentalAPI
@JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    // dependencies
    val actionsManager: ActionsManager by inject()
    val messageManager: MessageManager by inject()
    val clientsManager: ClientsManager by inject()

    routing {

        webSocket("/chat") {

            val client = ChatClient(this)
            clientsManager.addClient(client)

            try {
                while (true) {
                    val frame = incoming.receive()
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            when {
                                text.startsWith("/") -> actionsManager.performAction(client, text)
                                else -> messageManager.shareMessage(client, text)
                            }
                        }
                    }
                }
            } finally {
                clientsManager.removeClient(client)
            }
        }
    }
}
