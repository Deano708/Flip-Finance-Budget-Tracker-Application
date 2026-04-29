package com.example.flipfinance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(text)
        }
    }
}