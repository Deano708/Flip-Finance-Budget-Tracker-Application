package com.example.flipfinance.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/*
   Title: Rows, Columns & Basic Sizing - Android Jetpack Compose - Part 2
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 29/04/2026
   Code version : 1
   Availability: https://youtu.be/rHKeRWK3zL4?si=BIcdBEid7DIozjYu
*/

/*
   Title: Modifiers - Android Jetpack Compose - Part 3
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 29/04/2026
   Code version : 1
   Availability: https://youtu.be/XCuC_p3E0qo?si=e-mzwWJ2Dx5MDG5W
*/

/*
   Title: Textfields, Buttons & Showing Snackbars - Android Jetpack Compose - Part 7
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 29/04/2026
   Code version : 1
   Availability: https://youtu.be/_yON9d9if6g?si=SzA1f3U4XmFhxOUw
*/

@Composable
fun ToggleSettingRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        )
    }
}