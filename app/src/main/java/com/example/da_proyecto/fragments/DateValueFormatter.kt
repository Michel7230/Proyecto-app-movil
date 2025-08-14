package com.example.da_proyecto.fragments

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateValueFormatter(private val referenceTime: Long): ValueFormatter() {
    private val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
    override fun getFormattedValue(value: Float): String {
        val millis = referenceTime + (value * 1000).toLong()
        return sdf.format(Date(millis))
    }
}