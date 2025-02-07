package com.example.ecocafeconnect.Pages

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ecocafeconnect.AuthState
import com.example.ecocafeconnect.AuthViewModel
import com.example.ecocafeconnect.R
import com.example.ecocafeconnect.ui.theme.Purple40

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, context: Context) {

    val authState by authViewModel.authstate.observeAsState()

    // SharedPreferences initialization
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.a), contentDescription = "Settings Image",
                modifier = Modifier.size(392.dp)
            )

            Text(text = "Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold)

            TextButton(onClick = {
                // Sign out and clear shared preferences
                authViewModel.signout()

                // Clear SharedPreferences
                val editor = sharedPreferences.edit()
                editor.clear()  // Clears all stored data
                editor.apply()  // Apply changes
            }) {
                Text(text = "Sign Out and Exit")
            }
        }
    }
}
