package com.example.app_room

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

// pantalla principal de la app, aqui esta todo el crud
@Composable
fun TareaScreen(viewModel: TareaViewModel) {
    // observa la lista del viewmodel, cuando cambia la pantalla se redibuja sola
    val tareas by viewModel.tareas.collectAsState()

    // texto que se esta escribiendo en el campo de nueva tarea
    // remembersaveable conserva el valor aunque se rote la pantalla
    var nuevoTitulo by rememberSaveable { mutableStateOf("") }
    // id de la tarea que se esta editando, null si no se edita ninguna
    var idEnEdicion by rememberSaveable { mutableStateOf<Int?>(null) }

    // accion para crear la tarea y limpiar el campo de texto
    val agregar = {
        viewModel.agregar(nuevoTitulo)
        nuevoTitulo = ""
    }

    // scaffold da la estructura base de la pantalla
    // innerpadding es el espacio que ocupan las barras del sistema
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        // column pone los elementos uno debajo del otro
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // evita que ese espacio se cuente dos veces con el del teclado
                .consumeWindowInsets(innerPadding)
                // deja espacio cuando aparece el teclado
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // titulo de la pantalla
            Text(text = "Mis tareas", style = MaterialTheme.typography.headlineMedium)

            // row pone el campo de texto y el boton uno al lado del otro
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // campo para escribir la nueva tarea
                OutlinedTextField(
                    value = nuevoTitulo,
                    onValueChange = { nuevoTitulo = it },
                    label = { Text("Nueva tarea") },
                    singleLine = true,
                    // el teclado empieza en mayuscula y muestra el boton listo
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    // al pulsar listo se agrega la tarea si el titulo no esta vacio
                    keyboardActions = KeyboardActions(
                        onDone = { if (nuevoTitulo.isNotBlank()) agregar() }
                    ),
                    // weight hace que el campo ocupe todo el espacio que sobra
                    modifier = Modifier.weight(1f)
                )
                // create: el boton se deshabilita si el titulo esta vacio o solo tiene espacios
                Button(onClick = agregar, enabled = nuevoTitulo.isNotBlank()) {
                    Text("Agregar")
                }
            }

            // read: si no hay tareas muestra un mensaje, si hay muestra la lista
            if (tareas.isEmpty()) {
                // box centra el mensaje en el espacio disponible
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Aún no hay tareas",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // lazycolumn es una lista que solo dibuja lo que se ve en pantalla
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    // la key le permite a compose identificar cada fila aunque cambie la lista
                    items(tareas, key = { it.id }) { tarea ->
                        TareaItem(
                            tarea = tarea,
                            onHechaChange = { viewModel.cambiarHecha(tarea, it) },
                            onEditar = { idEnEdicion = tarea.id },
                            onEliminar = { viewModel.eliminar(tarea) }
                        )
                    }
                }
            }
        }
    }

    // busca la tarea en edicion, si existe muestra el dialogo
    tareas.find { it.id == idEnEdicion }?.let { tarea ->
        EditarTareaDialog(
            tarea = tarea,
            // guarda el titulo nuevo y cierra el dialogo
            onGuardar = { titulo ->
                viewModel.editarTitulo(tarea, titulo)
                idEnEdicion = null
            },
            // cierra el dialogo sin guardar
            onCancelar = { idEnEdicion = null }
        )
    }
}

// fila de la lista que muestra una tarea
// recibe funciones para avisar a la pantalla lo que pulsa el usuario
@Composable
private fun TareaItem(
    tarea: Tarea,
    onHechaChange: (Boolean) -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    // card es la tarjeta que agrupa la informacion de la tarea
    Card(modifier = Modifier.fillMaxWidth()) {
        // parte de arriba: checkbox y titulo
        Row(
            modifier = Modifier.padding(top = 4.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // update: marca o desmarca la tarea como hecha
            Checkbox(checked = tarea.hecha, onCheckedChange = onHechaChange)
            Text(
                text = tarea.titulo,
                style = MaterialTheme.typography.bodyLarge,
                // si la tarea esta hecha el titulo aparece tachado
                textDecoration = if (tarea.hecha) TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f)
            )
        }
        // parte de abajo: botones alineados a la derecha
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            // update: abre el dialogo de edicion
            TextButton(onClick = onEditar) { Text("Editar") }
            // delete: elimina la tarea
            TextButton(onClick = onEliminar) { Text("Eliminar") }
        }
    }
}

// dialogo para editar el titulo de una tarea
@Composable
private fun EditarTareaDialog(
    tarea: Tarea,
    onGuardar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    // texto del campo, empieza con el titulo actual de la tarea
    var titulo by rememberSaveable(tarea.id) { mutableStateOf(tarea.titulo) }

    AlertDialog(
        // se ejecuta si el usuario toca fuera del dialogo o pulsa atras
        onDismissRequest = onCancelar,
        title = { Text("Editar tarea") },
        text = {
            // campo para cambiar el titulo
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
        },
        // guardar se deshabilita si el titulo queda vacio
        confirmButton = {
            TextButton(onClick = { onGuardar(titulo) }, enabled = titulo.isNotBlank()) {
                Text("Guardar")
            }
        },
        // cancelar cierra el dialogo sin cambios
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
