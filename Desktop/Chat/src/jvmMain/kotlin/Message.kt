import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Date

data class Message(
    val content: String,
    val time: String,
    val fromContact: Boolean
)

@Composable
fun Message(
    message: Message,
    modifier: Modifier = Modifier
)
{
    Box(
        contentAlignment = when (message.fromContact) {
            true -> Alignment.CenterStart
            false -> Alignment.CenterEnd
        },
        modifier = modifier.fillMaxWidth()
    )
    {
        Card(
            modifier = Modifier.width(IntrinsicSize.Min)
        )
        {
            Column(
                modifier = Modifier.padding(15.dp)
            )
            {
                Text(
                    text = message.content,
                    fontSize = 16.sp
                )

                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    Text(
                        text = message.time,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun Messages(
    contact: Contact,
    id: String,
    modifier: Modifier = Modifier
)
{
    var currentMessage by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = contact.name)
                }
            )
        },
        modifier = modifier
    )
    {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        )
        {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            {
                items(contact.messages) { message ->
                    Message(message)
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
            )
            {
                val coroutineScope = rememberCoroutineScope()

                TextField(
                    value = currentMessage,
                    onValueChange = {
                        currentMessage = it
                    },
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        val currentTime = SimpleDateFormat("HH:mm").format(Date())
                        val message = JsonObject().apply {
                            addProperty("contact", id)
                            addProperty("message", currentMessage)
                            addProperty("time", currentTime)
                        }

                        contact.messages.add(
                            Message(
                                content = currentMessage,
                                time = currentTime,
                                fromContact = false
                            )
                        )
                        currentMessage = ""

                        coroutineScope.launch(Dispatchers.IO) {
                            val socket = Socket(contact.ipAddress, PORT)
                            with(DataOutputStream(socket.getOutputStream())) {
                                writeUTF(message.toString())
                                flush()
                                close()
                            }
                            socket.close()
                        }
                    }
                )
                {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
