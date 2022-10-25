import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.StandardSocketOptions

const val PORT = 12345

@Composable
fun App(
    id: String
)
{
    val contacts = remember {
        mutableStateMapOf(
            "Android" to Contact(
                name = "Android",
                ipAddress = "10.42.0.233"
            )
        )
    }
    var selectedContact by remember {
        mutableStateOf(contacts["Android"]!!)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            while(true) {
                val serverSocket = ServerSocket()
                serverSocket.setOption(StandardSocketOptions.SO_REUSEPORT, true)
                serverSocket.bind(InetSocketAddress(12345))
                val socket = serverSocket.accept()

                with(DataInputStream(socket.getInputStream())) {
                    val message = Gson().fromJson(readUTF(), JsonObject::class.java)
                    val contact = message.get("contact").asString

                    contacts[contact]?.messages?.add(
                        Message(
                            content = message.get("message").asString,
                            time = message.get("time").asString,
                            fromContact = true
                        )
                    )
                }

                serverSocket.close()
                socket.close()
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxSize()
    )
    {
        Contacts(
            contacts = contacts,
            onContactClicked = {
                selectedContact = it
            },
            modifier = Modifier.weight(1f)
        )

        Divider(
            modifier = Modifier.width(5.dp).fillMaxHeight()
        )

        Messages(
            contact = selectedContact,
            id = id,
            modifier = Modifier.weight(2f)
        )
    }
}
