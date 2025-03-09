package com.example.sprint0nj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.widget.Toast // "Toast" is an Android API used to display the short confirmation messages after clicking the buttons
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavHost()  // Show your NavHost here
        }
    }
}

@Composable
fun LibraryScreen(navController: NavHostController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)) // Green background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "My Lists",
            fontSize = 28.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        //TAKES YOU TO HOME SCREEN
        /*
        Button(onClick = { navController.navigate("home") }) {
            Text("Go to Home")
        }
        */

        // A Box is used as a container that fills the available width
        // The contentAlignment parameter ensures that the children (the plus button) is positioned at the top-right of the Box
        // "Toast" is an Android API used to display the short confirmation messages after clicking the buttons
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            // Call the reusable PlusButtonWithMenu composable
            // List of MenuOption objects is passed to define the menu items
            PlusButtonWithMenu(
                menuOptions = listOf(
                    // First menu option with the title "Add Playlist"
                    // When clicked, a Toast message is displayed
                    MenuOption("Add Playlist") {
                        Toast.makeText(context, "Add Playlist clicked", Toast.LENGTH_SHORT).show()
                    },
                    // Second menu option with the title "Import Playlist"
                    // When clicked, a Toast message is displayed
                    MenuOption("Import Playlist") {
                        Toast.makeText(context, "Import Playlist clicked", Toast.LENGTH_SHORT).show()
                    }
                )
            )

        }


        Spacer(modifier = Modifier.height(16.dp))

        val playlists = listOf("Playlist 1", "Playlist 2", "Playlist 3", "Playlist 4", "Playlist 5")

        playlists.forEach { playlistName ->
            Button(      // Navigate to the WorkoutScreen route
                onClick = {
                    navController.navigate("workout")
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = playlistName, fontSize = 16.sp, color = Color.Black)
            }
        }
    }
}
