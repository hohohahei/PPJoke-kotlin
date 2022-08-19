package com.example.ppjoke

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ppjoke.databinding.ActivityMainBinding
import com.example.ppjoke.ext.saveAs
import com.example.ppjoke.ui.binding_action.InteractionPresenter
import com.example.ppjoke.ui.binding_action.InteractionPresenter.checkIsLogin
import com.example.ppjoke.ui.couch.CouchFragment
import com.example.ppjoke.ui.discover.DiscoverFragment
import com.example.ppjoke.ui.feed.FeedFragment
import com.example.ppjoke.ui.my.MyFragment
import com.example.ppjoke.ui.publish.PublishFragment
import com.example.ppjoke.utils.MMKVUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.xtc.base.utils.toastShort
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController
    private val homeFragment = FeedFragment.newInstance(feedType = FeedFragment.FEED_TAG_VIDEO, type = FeedFragment.TYPE_COUCH)
    private val couchFragment = CouchFragment()
    private val publishFragment=PublishFragment()
    private val discoverFragment = DiscoverFragment()
    private val myFragment = MyFragment()

    //当前展示的fragment
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId=MMKVUtils.getInstance().getUserId()
        val isLogin=userId!=null&&userId!= 0L
        MMKVUtils.getInstance().encode("isLogin",isLogin)
        navView = binding.navView

        val navHost: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view)?.saveAs<NavHostFragment>()!!

        navController = navHost.navController
        navController.let { navView.setupWithNavController(it) }
        initFragmentAdd()

    }


    private fun initFragmentAdd() {
        initLazyFragment()
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    showFragment(homeFragment)
                }
                R.id.navigation_couch -> {
                    showFragment(couchFragment)
                }
                R.id.navigation_publish->{
                    if (checkIsLogin(this)) {
                        showFragment(publishFragment)
                    }
                }
                R.id.navigation_discover -> {
                    showFragment(discoverFragment)
                }
                R.id.navigation_my -> {
                    if (checkIsLogin(this)) {
                        showFragment(myFragment)
                    }
                }
            }
            return@setOnItemSelectedListener true
        }

    }

    //通过setMaxLifecycle让Fragment可以懒加载实现
    private fun initLazyFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, homeFragment, "navigation_home").commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, couchFragment, "navigation_cover")
            .setMaxLifecycle(couchFragment, Lifecycle.State.STARTED).hide(couchFragment).commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view,publishFragment,"navigation_publish")
            .setMaxLifecycle(publishFragment,Lifecycle.State.STARTED).hide(publishFragment).commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, discoverFragment, "navigation_discover")
            .setMaxLifecycle(discoverFragment, Lifecycle.State.STARTED).hide(discoverFragment)
            .commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, myFragment, "navigation_my")
            .setMaxLifecycle(myFragment, Lifecycle.State.STARTED).hide(myFragment).commit()

    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            hide(activeFragment)
            setMaxLifecycle(activeFragment, Lifecycle.State.STARTED)
            show(fragment)
            setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
        }.commit()
        activeFragment = fragment
    }

    private val WAIT_TIME = 2000L
    private var TOUCH_TIME = 0L

    override fun onBackPressed() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            finish()
            exitProcess(0)
        } else {
            TOUCH_TIME = System.currentTimeMillis()
            toastShort("再按一次退出")
        }
    }
}