package com.example.flipfinance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/*
   Title: Rows, Columns & Basic Sizing - Android Jetpack Compose - Part 2
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 18/04/2026
   Code version : 1
   Availability: https://youtu.be/rHKeRWK3zL4?si=BIcdBEid7DIozjYu
*/

/*
   Title: Modifiers - Android Jetpack Compose - Part 3
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 18/04/2026
   Code version : 1
   Availability: https://youtu.be/XCuC_p3E0qo?si=e-mzwWJ2Dx5MDG5W
*/

/*
   Title: Textfields, Buttons & Showing Snackbars - Android Jetpack Compose - Part 7
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 18/04/2026
   Code version : 1
   Availability: https://youtu.be/_yON9d9if6g?si=SzA1f3U4XmFhxOUw
*/

@Composable
fun PrimaryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true
    )
}