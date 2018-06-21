package com.example.mylibrary

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

open class ToolbarActivity : AppCompatActivity(), CustomToolbar {

    protected var _toolbar: Toolbar? = null

    override fun loadToolbar(toolbar: Toolbar?) {
        _toolbar = toolbar
        _toolbar?.let {
            setSupportActionBar(_toolbar)
        }
    }

    override fun enableHomeDisplay(value: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(value)
    }
}