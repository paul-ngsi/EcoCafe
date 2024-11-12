package com.example.ecocafeconnect.Pages.HomePage

import android.os.Build
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ecocafeconnect.AuthState
import com.example.ecocafeconnect.AuthViewModel
import com.example.ecocafeconnect.wasteTracker.WasteEntry
import com.example.ecocafeconnect.wasteTracker.WasteEntryListViewModel
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WasteTracker(modifier: Modifier = Modifier,navController: NavController, viewModel: WasteEntryListViewModel, authViewModel: AuthViewModel) {

    val authState = authViewModel.authstate.observeAsState()
    val wasteEntries by viewModel.wasteEntries.observeAsState(listOf())
    val context = LocalContext.current

    val (date, setDate) = remember { mutableStateOf(LocalDate.now()) }
    val type = remember { mutableStateOf("") }
    val amount = remember { mutableStateOf("") } // changed to 0.0 to allow decimal numbers
    val wasteEntryId = remember { mutableStateOf("") }
    val showToast = remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = "Waste Tracker", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Add New Waste Entry:", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = "${date.monthValue}/${date.dayOfMonth}/${date.year}",
            onValueChange = { },
            label = { Text("Date") },
            modifier = Modifier.width(225.dp).height(75.dp),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Select Date")
                }
            }
        )

        if (showDatePicker) {
            DatePickerDocked(
                date = date,
                onDateChange = { setDate(it) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = type.value,
            onValueChange = { type.value = it },
            label = { Text("Type of waste") },
            modifier = Modifier.width(225.dp).height(75.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = amount.value.toString(),
            onValueChange = { amount.value = it },
            label = { Text("Amount in kg") },
            modifier = Modifier.width(225.dp).height(75.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            keyboardActions = KeyboardActions(onDone = { /* optional */ })
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            modifier = Modifier
                .width(180.dp) // Increase the width
                .height(50.dp), // Increase the height
            onClick = {
                val newWasteEntry = WasteEntry(
                    id = "",
                    date = "${date.monthValue}/${date.dayOfMonth}/${date.year}",
                    type = type.value,
                    amount = amount.value.toDoubleOrNull() ?: 0.0
                )
                viewModel.addWasteEntry(newWasteEntry)
                Log.d("WasteTracker", "Add Waste Entry button clicked")
                Log.d("WasteTracker", "newWasteEntry: $newWasteEntry")
                showToast.value = true
            }
        ) {
            Text(
                text = "Add Waste Entry",
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "Waste Entry List:", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(5.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            items(wasteEntries) { wasteEntry ->
                TodoItem(wasteEntry, onDelete = {
                    viewModel.deleteWasteEntry(wasteEntry.id)
                }, viewModel = viewModel)
            }
        }

        if (showToast.value) {
            LaunchedEffect(Unit) {
                Toast.makeText(context, "Waste entry added successfully", Toast.LENGTH_SHORT).show()
                showToast.value = false
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerDocked(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = remember { mutableStateOf(date) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = "${datePickerState.value.monthValue}/${datePickerState.value.dayOfMonth}/${datePickerState.value.year}",
            onValueChange = { },
            label = { Text("Date") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        if (showDatePicker) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                DatePicker(
                    state = datePickerState.value,
                    onDateChange = { datePickerState.value = it; onDateChange(it) }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker(
    state: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    val daysInMonth = state.lengthOfMonth()
    val firstDayOfWeek = state.withDayOfMonth(1).dayOfWeek.value

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${state.monthValue}/${state.year}")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Sun")
            Text(text = "Mon")
            Text(text = "Tue")
            Text(text = "Wed")
            Text(text = "Thu")
            Text(text = "Fri")
            Text(text = "Sat")
        }
        val rows = (daysInMonth + firstDayOfWeek - 1 + 6) / 7 // Calculate the number of rows
        for (i in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (j in 0 until 7) {
                    val day = i * 7 + j - firstDayOfWeek + 1
                    if (day > 0 && day <= daysInMonth) {
                        Box(
                            modifier = Modifier
                                .size(25.dp, 25.dp) // Set button size to 25x25 dp
                                .background(Color.Transparent) // Set background color to gray
                                .clickable {
                                    onDateChange(state.withDayOfMonth(day))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                fontSize = 20.sp, // Reduce font size
                                color = Color.Black // Set text color to black
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(25.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun WasteEntryItem(wasteEntry: WasteEntry, onDelete: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = wasteEntry.date)
        Text(text = wasteEntry.type)
        Text(text = wasteEntry.amount.toString())
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete")
        }
    }
}

@Composable
fun TodoItem(todoItem: WasteEntry, onDelete: () -> Unit, viewModel: WasteEntryListViewModel) {
    Log.d("WasteTracker", "TodoItem: $todoItem") // Log the todoItem data

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // Padding around the card

    ) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp) // Padding inside the card
        ) {
            Text(
                text = todoItem.date,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold // Make the date bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), // Vertical padding for spacing
                horizontalArrangement = Arrangement.Center // Center the contents horizontally
            ) {
                Column {
                    Text(
                        text = todoItem.type,
                        fontSize = 25.sp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = todoItem.amount.toString() + " kg",
                        fontSize = 25.sp
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp), // Padding for the delete button
            contentAlignment = Alignment.BottomEnd // Align the delete button to the bottom right
        ) {
            IconButton(onClick = { onDelete?.invoke() }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}