@file:Suppress("UNUSED_VARIABLE", "unused", "UNUSED_PARAMETER")

package com.bammellab.gamepadtest

import android.os.Bundle
import android.util.Log
import android.view.InputDevice
import android.view.KeyEvent
import android.view.Menu
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.bammellab.gamepadtest.gamepad.GamepadServices

class MainActivity : AppCompatActivity(), LifecycleObserver {

    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_scan, R.id.navigation_gamepad, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            val toolBar = supportActionBar ?: return@addOnDestinationChangedListener
            when (destination.id) {

                R.id.navigation_gamepad -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    toolBar.title = getString(R.string.app_name)
                }
                R.id.navigation_scan -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                }
                else -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                }
            }
        }
    }

    /**
     * handle the key events -
     *    Don't pass the key event to the framework
     *    or else the Bottom Navigation gets active
     *    when it should not.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        handleSystemKeys(keyCode, event)
        GamepadServices.gamepadButtonService.forwardButtonDown(keyCode, event)
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        GamepadServices.gamepadButtonService.forwardButtonUp(keyCode, event)
        return true
    }

    /**
     * handle the events from the:
     *  -> joysticks
     *  -> Dpad (they are motion events, I don't see key Events from the TRUST MXP gamepad)
     *  -> Triggers
     *
     *  The historical position events don't seem to have much value.
     *  Maybe they are more immportant for a stylus and graphix pad or somethimg.
     */
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {

        val joyst = GamepadServices.gamepadJoystickService

        // Check that the event came from a game controller
        return if (event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK
            && event.action == MotionEvent.ACTION_MOVE
        ) {
            joyst.processJoystickInput(this, event)
            true
        } else {
            super.onGenericMotionEvent(event)
        }
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        Log.i("GAMEPAD", "onMENU OPENED *******************")
        return super.onMenuOpened(featureId, menu)
    }

    /**
     * Examine key events for system functions.
     *
     * TODO: vector back key to one fragment first, then exit.  Right now just exit
     *
     */
    private fun handleSystemKeys(keyCode: Int, event: KeyEvent?) {
        if (event == null) {
            return
        }
        val source = event.source
        val flags = event.flags
        val sys = KeyEvent.FLAG_FROM_SYSTEM

        if (source == 257) {  // not from any game controller
            if ((flags and sys) == sys) { // is a system keycode
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish()
                }
            }
        }
    }
}