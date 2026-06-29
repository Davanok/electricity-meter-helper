package com.davanok.electricitymeterhelper.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char

val DateTimeFormat = LocalDateTime.Format {
    day()
    char('-')
    monthNumber()
    char('-')
    year()
    char(' ')
    hour()
    char(':')
    minute()
}