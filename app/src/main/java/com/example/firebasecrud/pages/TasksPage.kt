package com.example.firebasecrud.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.firebasecrud.data.Task
import com.example.firebasecrud.viewmodel.TaskViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksPage(modifier: Modifier, navController: NavController, taskViewModel: TaskViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var editingTaskId by remember { mutableStateOf<String?>(null) }

    val tasks by taskViewModel.tasks.observeAsState(emptyList())
    val loading by taskViewModel.loading.observeAsState(false)
    val error by taskViewModel.error.observeAsState()

    LaunchedEffect(Unit) {
        taskViewModel.getAll()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Gestão de Tarefas", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("home") {
                            popUpTo("tasks") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Blue
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedButton(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        val task = Task(
                            id = editingTaskId ?: UUID.randomUUID().toString(),
                            title = title,
                            description = description
                        )
                        if (editingTaskId == null) {
                            taskViewModel.save(task)
                        } else {
                            taskViewModel.update(task)
                        }
                        title = ""
                        description = ""
                        editingTaskId = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text(
                    text = if (editingTaskId != null) "Atualizar Tarefa" else "Cadastrar Tarefa",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            error?.let {
                Text(text = it, color = Color.Red)
            }

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(tasks) { task ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(task.title, style = MaterialTheme.typography.bodyLarge)
                                Text(task.description, style = MaterialTheme.typography.bodyMedium)

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TextButton(onClick = {
                                        title = task.title
                                        description = task.description
                                        editingTaskId = task.id
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                        Text("Editar")
                                    }

                                    TextButton(onClick = {
                                        taskViewModel.delete(task.id)
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Excluir")
                                        Text("Excluir")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}