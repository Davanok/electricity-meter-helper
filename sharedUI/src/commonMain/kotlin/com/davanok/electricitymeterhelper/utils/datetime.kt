package com.davanok.electricitymeterhelper.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.char

val DateFormat = LocalDate.Format {
    day()
    char('-')
    monthNumber()
    char('-')
    year()
}