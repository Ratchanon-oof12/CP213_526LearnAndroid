package com.example.a526lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.example.a526lablearnandroid.util.PokemonEntry

class PokedexActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 1. Get the ViewModel
            val viewModel: PokemonViewModel = viewModel()

            // 2. Trigger the API call once when the screen is first shown
            androidx.compose.runtime.LaunchedEffect(Unit) {
                viewModel.fetchPokemon()
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Pokedex") },
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Red,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                }
            ) { paddingValues ->
                // 3. Pass the ViewModel down to the screen
                ListScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun ListScreen(
    viewModel: PokemonViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    // 4. Observe the pokemon list from the ViewModel
    val pokemonList by viewModel.pokemonList.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Red)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
                .padding(16.dp)
        ) {
            // 5. Show a loading spinner while the list is empty, otherwise show the list
            if (pokemonList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Red)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    items(pokemonList) { item ->
                        PokemonRow(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonRow(item: PokemonEntry) {
    val imageUrl =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${item.entry_number}.png"

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = "#${item.entry_number}")
        Spacer(modifier = Modifier.width(16.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .listener(
                    onStart = { Log.d("AsyncImage", "Start loading: $imageUrl") },
                    onError = { _, result ->
                        Log.e("AsyncImage", "Error loading: $imageUrl", result.throwable)
                    },
                    onSuccess = { _, _ ->
                        Log.d("AsyncImage", "Success loading: $imageUrl")
                    }
                )
                .build(),
            contentDescription = "Sprite of ${item.pokemon_species.name}",
            modifier = Modifier.size(64.dp),
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
            error = painterResource(id = R.drawable.ic_launcher_background)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = item.pokemon_species.name.replaceFirstChar { it.uppercase() })
    }
}

@Preview(showBackground = true)
@Composable
fun ListPreview() {
    ListScreen()
}