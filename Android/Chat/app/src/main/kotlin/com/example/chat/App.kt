package com.example.chat

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataInputStream
import java.net.ServerSocket

const val ID = "Android"
const val PORT = 12345

@Composable
fun App()
{
    lateinit var selectedContact: Contact
    val contacts = remember {
        mutableStateMapOf(
            "Notebook 1" to Contact(
                name = "Notebook 1",
                ipAddress = "10.42.0.1"
            ),
            "Notebook 2" to Contact(
                name = "Notebook 2",
                ipAddress = "10.42.0.1"
            )
        )
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            while(true) {
                val serverSocket = ServerSocket(PORT)
                val socket = serverSocket.accept()

                with(DataInputStream(socket.getInputStream())) {
                    val message = JSONObject(readUTF())
                    val contact = message.getString("contact")

                    contacts[contact]?.messages?.add(
                        Message(
                            content = message.getString("message"),
                            time = message.getString("time"),
                            fromContact = true
                        )
                    )
                }

                serverSocket.close()
                socket.close()
            }
        }
    }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "contacts"
    )
    {
        composable(route = "contacts") {
            Contacts(
                contacts = contacts,
                onContactClicked = {
                    selectedContact = it
                    navController.navigate("messages")
                }
            )
        }

        composable(route = "messages") {
            Messages(
                contact = selectedContact
            )
        }
    }
}
