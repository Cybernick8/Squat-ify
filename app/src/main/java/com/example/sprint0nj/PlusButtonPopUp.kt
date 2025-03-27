package com.example.sprint0nj

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*

import androidx.compose.ui.text.input.TextFieldValue
import android.widget.Toast

import androidx.compose.ui.platform.LocalContext
import com.example.sprint0nj.data.Classes.Workout
import com.example.sprint0nj.data.Classes.Playlist
import com.example.sprint0nj.data.FirestoreRepository
import kotlinx.coroutines.launch
import java.util.UUID


// Data class representing a single menu option
// This class holds the title (what is displayed in the menu) and an action (lambda) to execute on click
data class MenuOption(
    val title: String,       // The text displayed for the menu option
    val onClick: () -> Unit  // The action executed when the option is selected
)

@Composable
fun PlaylistNameDialog(
    onDismiss: () -> Unit,      // Called to dismiss the dialog
    onConfirm: (String) -> Unit // Called with the entered playlist name when confirmed
) {
    var playlistName by remember { mutableStateOf(TextFieldValue("")) }
    val firestoreRepository = remember { FirestoreRepository() }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Playlist Name:") },
        text = {
            // BasicTextField to allow the user to type in the playlist name
            BasicTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        },
        confirmButton = {
            // Confirm button returns the entered playlist name
            Button(
                onClick = {
                    val playlist = Playlist(
                        id = UUID.randomUUID().toString(),
                        name = playlistName.text
                    )
                    firestoreRepository.postPlaylist(playlist)
                    onConfirm(playlistName.text) // Pass the input to the onConfirm callback
                    onDismiss() // Close the dialog after confirming
                },

                 colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                // Can also modify the shape, padding, etc.
            ) {
                // Customize the text style here (font size, color, etc.)
                Text("Confirm")
            }
        },
        dismissButton = {
            // Cancel Button: Customize its UI similarly to the Confirm Button
            Button(
                onClick = { onDismiss() },
                // For changing the Cancel button's appearance
                 colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                // Can add any padding or shape modifications here.
            ) {
                Text("Cancel")
            }
        }
    )
}


// Data class representing a workout entry with its configuration.
data class WorkoutEntry(
    val name: String,
    val reps: Int,
    val sets: Int
)

@Composable
fun WorkoutSelectionDialog(
    playlist: Playlist,
    onDismiss: () -> Unit,
    onConfirm: (WorkoutEntry) -> Unit
) {
    var selectedWorkout by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }
    var setsText by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val availableWorkouts = remember { mutableStateOf<List<Workout>>(emptyList())}
    val firestoreRepository = remember {FirestoreRepository()}
//    val playlist = remember { mutableStateOf<Playlist?>(null) }

    LaunchedEffect(Unit){
        availableWorkouts.value = firestoreRepository.fetchWorkouts()
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        //title = { Text("Select Workout:") }, // Optional title if needed.
        text = {
            Column {
                // Dropdown for selecting a workout.
                Box {
                    Button(
                        onClick = { isDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth()


                    ) {
                        Text(
                            text = selectedWorkout.ifEmpty { "Select workout" },
                            fontSize = 16.sp

                        )
                    }
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        availableWorkouts.value.forEach { workout ->
                            DropdownMenuItem(
                                text = { Text(workout.title) },
                                onClick = {
                                    selectedWorkout = workout.title
                                    isDropdownExpanded = false
                                    repsText = workout.reps?.toString() ?: "-"
                                    setsText = workout.sets?.toString() ?: "-"
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // TextField for entering the number of Reps.
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { repsText = it },
                    label = { Text("Reps") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // TextField for entering the number of Sets.
                OutlinedTextField(
                    value = setsText,
                    onValueChange = { setsText = it },
                    label = { Text("Sets") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Ensure a workout is selected and convert reps/sets safely to integers.
                    if (selectedWorkout.isNotEmpty()) {
                        val reps = repsText.toIntOrNull() ?: 0
                        val sets = setsText.toIntOrNull() ?: 0
                        playlist.workouts.add(Workout(UUID.randomUUID().toString(), selectedWorkout, null, reps, sets, ""))
                        firestoreRepository.postPlaylist(playlist)
                        onConfirm(WorkoutEntry(selectedWorkout, reps, sets))
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Confirm", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}



// Reusable composable for the plus button with a popup (dropdown) menu
// This component can be used in multiple screens by passing different lists of MenuOption items
@Composable
fun PlusButtonWithMenu(
    menuOptions: List<MenuOption>  // A list of menu options to display in the dropdown
) {
    // Local state to track whether the dropdown menu is currently expanded
    var menuExpanded by remember { mutableStateOf(false) }
    // State to control the visibility of the Playlist dialog
    var showPlaylistDialog by remember { mutableStateOf(false) }
    // State to control the visibility of the Workout dialog
    var showWorkoutDialog by remember { mutableStateOf(false) }

    // Capture the context once in this composable scope
    val context = LocalContext.current

    // Box is used as a container to anchor both the plus button and its dropdown menu
    // The wrapContentSize with Alignment.TopEnd places content at the top-right corner
    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {


       // Plus Button:
        // This button displays a "+" symbol and triggers the dropdown menu when clicked
        Button(
            onClick = { menuExpanded = true }, // When clicked, set menuExpanded to true to open the menu
            modifier = Modifier.size(56.dp),     // Set the fixed size of the button (can be adjusted).
            shape = RoundedCornerShape(12.dp),   // Rounded corners. Can change the dp value to alter curvature
            colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Button background color
            contentPadding = PaddingValues(0.dp)  // Remove any internal padding for a tighter layout
        ) {

            // Inner Box to center the "+" text inside the button
            Box(
                modifier = Modifier.fillMaxSize(),        // Fill the available space inside the button
                contentAlignment = Alignment.Center         // Center the text both vertically and horizontally
            ) {
                // Text displaying the plus sign
                // Can adjust fontSize and fontWeight for customization
                Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }


        // DropdownMenu:
        // This menu appears when menuExpanded is true and displays the list of menu options
        DropdownMenu(
            expanded = menuExpanded,                   // Controls whether the menu is visible
            onDismissRequest = { menuExpanded = false }, // Callback to close the menu when clicked outside
            // The offset positions the dropdown menu relative to the plus button
            // Change the DpOffset values to adjust horizontal (x) or vertical (y) position
            offset = DpOffset(0.dp, 0.dp)
        ) {
            // Loop through each menu option provided in the list
            menuOptions.forEach { option ->
                // Each option is displayed as a DropdownMenuItem
                DropdownMenuItem(
                    text = { Text(option.title) },
                    onClick = {
                        when (option.title) {
                            "Add Playlist" -> {
                                showPlaylistDialog = true // Show the playlist dialog
                            }
                            // "Add Workout" will now simply call the provided callback.
                            else -> {
                                option.onClick()
                            }
                        }
                        menuExpanded = false
                    }
                )
            }
        }

        // Display the PlaylistNameDialog when showPlaylistDialog is true
        if (showPlaylistDialog) {
            PlaylistNameDialog(
                onDismiss = { showPlaylistDialog = false },
                onConfirm = { playlistName ->

                    /*

                    Replace Toast with firestore integration to add the new playlist to database.


                    */
                    Toast.makeText(context, "Playlist added: $playlistName", Toast.LENGTH_SHORT).show()
                }
            )
        }

        /*if (showWorkoutDialog) {
            // Pass the workouts list from Firebase.
            WorkoutSelectionDialog(
                availableWorkouts = workoutsList.value,
                onDismiss = { showWorkoutDialog = false },
                onConfirm = { workoutEntry ->
                    // Replace the Toast with your Firestore integration code to add the workout.
                    Toast.makeText(context, "Workout added: ${workoutEntry.name} with ${workoutEntry.reps} reps and ${workoutEntry.sets} sets", Toast.LENGTH_SHORT).show()
                }
            )
        }*/
    }
}
