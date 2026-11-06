package com.example.teamtitans

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.teamtitans.ui.theme.TeamTitansTheme

// ACTIVIDAD 1: estructura de datos del jugador
data class Jugador(
    val id: Int,
    val nombre: String,
    val nickname: String,
    val rol: String,
    val imagen: Int
)

// La aplicación comienza con cinco jugadores
private fun jugadoresIniciales(): List<Jugador> {
    return listOf(
        Jugador(1, "AlexPro", "AlexPro", "Atacante", R.drawable.jugador1),
        Jugador(2, "ShadowX", "ShadowX", "Defensor", R.drawable.jugador2),
        Jugador(3, "MartaGG", "MartaGG", "Soporte", R.drawable.jugador3),
        Jugador(4, "Destroyer", "Destroyer", "Atacante", R.drawable.jugador4),
        Jugador(5, "Martha", "Martha", "Central", R.drawable.jugador5)
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TeamTitansTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PantallaPrincipal(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaPrincipal(modifier: Modifier = Modifier) {

    // ACTIVIDAD 1 y 4: lista dinámica que actualiza la interfaz
    val jugadores = remember {
        mutableStateListOf<Jugador>().apply {
            addAll(jugadoresIniciales())
        }
    }

    // ACTIVIDAD 2: estados del formulario.
    // rememberSaveable conserva estos campos ante un cambio de configuración.
    var nombreNuevo by rememberSaveable { mutableStateOf("") }
    var nicknameNuevo by rememberSaveable { mutableStateOf("") }
    var rolNuevo by rememberSaveable { mutableStateOf("") }
    var errorFormulario by rememberSaveable { mutableStateOf("") }

    // ACTIVIDADES 4 y 5: jugador seleccionado para cada operación
    var jugadorAEliminarId by remember { mutableStateOf<Int?>(null) }
    var jugadorAEditarId by remember { mutableStateOf<Int?>(null) }

    // Datos que se muestran en el formulario de edición
    var nombreEditado by remember { mutableStateOf("") }
    var nicknameEditado by remember { mutableStateOf("") }
    var rolEditado by remember { mutableStateOf("") }
    var errorEdicion by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TEAM TITANS",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    FormularioJugador(
                        nombre = nombreNuevo,
                        nickname = nicknameNuevo,
                        rol = rolNuevo,
                        error = errorFormulario,
                        onNombreChange = { nombreNuevo = it },
                        onNicknameChange = { nicknameNuevo = it },
                        onRolChange = { rolNuevo = it },
                        onAnadir = {
                            if (
                                nombreNuevo.isBlank() ||
                                nicknameNuevo.isBlank() ||
                                rolNuevo.isBlank()
                            ) {
                                errorFormulario = "Completa todos los campos"
                            } else {
                                val nuevoId =
                                    (jugadores.maxOfOrNull { it.id } ?: 0) + 1

                                jugadores.add(
                                    Jugador(
                                        id = nuevoId,
                                        nombre = nombreNuevo.trim(),
                                        nickname = nicknameNuevo.trim(),
                                        rol = rolNuevo.trim(),
                                        imagen = R.drawable.jugador1
                                    )
                                )

                                nombreNuevo = ""
                                nicknameNuevo = ""
                                rolNuevo = ""
                                errorFormulario = ""
                            }
                        }
                    )
                }
            }

            // ACTIVIDAD 3: items recorre la lista y key identifica cada jugador
            items(
                items = jugadores,
                key = { jugador -> jugador.id }
            ) { jugador ->
                TarjetaJugador(
                    jugador = jugador,
                    onEditar = {
                        jugadorAEditarId = jugador.id
                        nombreEditado = jugador.nombre
                        nicknameEditado = jugador.nickname
                        rolEditado = jugador.rol
                        errorEdicion = ""
                    },
                    onEliminar = {
                        jugadorAEliminarId = jugador.id
                    }
                )
            }
        }
    }

    // ACTIVIDAD 4: diálogo de confirmación para eliminar
    val jugadorAEliminar = jugadores.find {
        it.id == jugadorAEliminarId
    }

    if (jugadorAEliminar != null) {
        AlertDialog(
            onDismissRequest = {
                jugadorAEliminarId = null
            },
            title = {
                Text("Eliminar jugador")
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar a ${jugadorAEliminar.nickname}?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        jugadores.removeAll {
                            it.id == jugadorAEliminar.id
                        }
                        jugadorAEliminarId = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        jugadorAEliminarId = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // ACTIVIDAD 5: diálogo para editar los datos del jugador
    val jugadorAEditar = jugadores.find {
        it.id == jugadorAEditarId
    }

    if (jugadorAEditar != null) {
        DialogoEditarJugador(
            nombre = nombreEditado,
            nickname = nicknameEditado,
            rol = rolEditado,
            error = errorEdicion,
            onNombreChange = { nombreEditado = it },
            onNicknameChange = { nicknameEditado = it },
            onRolChange = { rolEditado = it },
            onGuardar = {
                if (
                    nombreEditado.isBlank() ||
                    nicknameEditado.isBlank() ||
                    rolEditado.isBlank()
                ) {
                    errorEdicion = "Completa todos los campos"
                } else {
                    val posicion = jugadores.indexOfFirst {
                        it.id == jugadorAEditar.id
                    }

                    if (posicion != -1) {
                        jugadores[posicion] = jugadorAEditar.copy(
                            nombre = nombreEditado.trim(),
                            nickname = nicknameEditado.trim(),
                            rol = rolEditado.trim()
                        )
                    }

                    jugadorAEditarId = null
                    errorEdicion = ""
                }
            },
            onCancelar = {
                jugadorAEditarId = null
                errorEdicion = ""
            }
        )
    }
}

// Formulario para añadir jugadores
@Composable
fun FormularioJugador(
    nombre: String,
    nickname: String,
    rol: String,
    error: String,
    onNombreChange: (String) -> Unit,
    onNicknameChange: (String) -> Unit,
    onRolChange: (String) -> Unit,
    onAnadir: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nickname,
            onValueChange = onNicknameChange,
            label = { Text("Nickname") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = rol,
            onValueChange = onRolChange,
            label = { Text("Rol") },
            modifier = Modifier.fillMaxWidth()
        )

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = onAnadir,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Añadir jugador")
        }
    }
}

// Tarjeta que muestra los datos y los botones de cada jugador
@Composable
fun TarjetaJugador(
    jugador: Jugador,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = jugador.imagen),
                contentDescription = "Imagen de ${jugador.nombre}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = jugador.nombre,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = "Nickname: ${jugador.nickname}")
                Text(text = "Rol: ${jugador.rol}")
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onEditar) {
                Text("Editar")
            }

            Button(onClick = onEliminar) {
                Text("Eliminar")
            }
        }
    }
}

// Diálogo con los campos editables del jugador
@Composable
fun DialogoEditarJugador(
    nombre: String,
    nickname: String,
    rol: String,
    error: String,
    onNombreChange: (String) -> Unit,
    onNicknameChange: (String) -> Unit,
    onRolChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text("Editar jugador")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = onNombreChange,
                    label = { Text("Nombre") }
                )

                OutlinedTextField(
                    value = nickname,
                    onValueChange = onNicknameChange,
                    label = { Text("Nickname") }
                )

                OutlinedTextField(
                    value = rol,
                    onValueChange = onRolChange,
                    label = { Text("Rol") }
                )

                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onGuardar) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}
