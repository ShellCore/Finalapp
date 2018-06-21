package com.example.mylibrary

import android.support.v7.widget.Toolbar


interface CustomToolbar {
    fun loadToolbar(toolbar: Toolbar?)
    fun enableHomeDisplay(value: Boolean)
}