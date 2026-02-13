package com.vyaparlite.utils

import java.text.DecimalFormat

private val money = DecimalFormat("#,##0.##")

fun formatMoney(symbol: String, value: Double): String = "$symbol ${money.format(value)}"
