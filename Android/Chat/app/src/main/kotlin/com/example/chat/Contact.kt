package com.example.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Contact(
    val name: String,
    val ipAddress: String
)
{
    val messages = mutableStateListOf<Message>()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Contact(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)
{
    Card(
        onClick = onClick,
        modifier = modifier
    )
    {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.fillMaxSize()
        )
        {
            Text(
                text = name,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Contacts(
    contacts: Map<String, Contact>,
    onContactClicked: (contact: Contact) -> Unit
)
{
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Contatos")
                }
            )
        }
    )
    {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        )
        {
            items(contacts.values.toList()) { contact ->
                Contact(
                    name = contact.name,
                    onClick = {
                        onContactClicked(contact)
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
