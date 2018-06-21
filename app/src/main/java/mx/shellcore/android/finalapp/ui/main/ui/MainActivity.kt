package mx.shellcore.android.finalapp.ui.main.ui

import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.example.mylibrary.ToolbarActivity
import kotlinx.android.synthetic.main.activity_main.*
import mx.shellcore.android.finalapp.R

class MainActivity : ToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadToolbar(toolbarView as Toolbar)
    }
}
