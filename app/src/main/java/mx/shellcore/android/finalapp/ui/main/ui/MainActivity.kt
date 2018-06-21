package mx.shellcore.android.finalapp.ui.main.ui

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.example.mylibrary.ToolbarActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.ui.main.ui.adapters.PagerAdapter
import mx.shellcore.android.finalapp.ui.main.ui.fragments.ChatFragment
import mx.shellcore.android.finalapp.ui.main.ui.fragments.InfoFragment
import mx.shellcore.android.finalapp.ui.main.ui.fragments.RatesFragment

class MainActivity : ToolbarActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var adapter: PagerAdapter

    private var prevButtonSelected: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadToolbar(toolbarView as Toolbar)
        setupViewPager(getPagerAdapter())
        setupBottomNavigationBar()
    }

    private fun getPagerAdapter(): PagerAdapter {
        adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(ChatFragment())
        adapter.addFragment(InfoFragment())
        adapter.addFragment(RatesFragment())
        return adapter
    }

    private fun setupViewPager(pager: PagerAdapter) {
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (prevButtonSelected == null) {
                    bottomNavigation.menu.getItem(0).isChecked = false
                } else {
                    prevButtonSelected!!.isChecked = false
                }
                bottomNavigation.menu.getItem(position).isChecked = true
                prevButtonSelected = bottomNavigation.menu.getItem(position)
            }
        })
    }

    private fun setupBottomNavigationBar() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_info -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.bottom_nav_rates -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.bottom_nav_chats -> {
                    viewPager.currentItem = 2
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}
