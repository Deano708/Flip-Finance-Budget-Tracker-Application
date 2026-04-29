package com.example.flipfinance.ui.home

fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance()

        .get(java.util.Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 0..11 -> "Good Morning"

        in 12..17 -> "Good Afternoon"

        else -> "Good Evening"
    }

}

fun extractNameFromEmail(email: String?): String {

    if (email == null) return "User"

    val namePart = email.substringBefore("@")

    return namePart.replaceFirstChar {
        it.uppercase()
    }
}