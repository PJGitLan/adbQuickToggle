package com.example.adbquicktoggle

import android.content.Context
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import java.io.DataOutputStream
import java.io.IOException
import java.lang.Exception

class MyTileService : TileService() {

    private val context: Context = this

    override fun onStartListening() {
        super.onStartListening()
        setTileState(getADBState())
    }

    private fun setTileState(adbState : Boolean){
        if (adbState){
            qsTile.state = Tile.STATE_ACTIVE
        }
        else if (!adbState) {
            qsTile.state = Tile.STATE_INACTIVE

        }
        qsTile.updateTile()
    }

    /*override fun onTileAdded() {
        super.onTileAdded()
        setState();
    }*/

    override fun onClick() {
        super.onClick()
        fun switchADB() {
            setADBState(!getADBState())
            val adbState = getADBState()
            setTileState(adbState)
            showADBState(adbState)
        }
        
        if(isLocked) unlockAndRun { switchADB() }
        else if(!isLocked) switchADB()

    }

    private fun showADBState(currState : Boolean) {
        var toastmsg = "ADB is "
        toastmsg += if(currState) "active!" else "inactive!"

        Toast.makeText(context, toastmsg, Toast.LENGTH_LONG).show()
    }

    private fun getADBState(): Boolean {
        val state = Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0)
        return state == 1
    }

    private fun setADBState(on: Boolean){
        val onInt = if (on) 1 else 0
        try {
            val su = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(su.outputStream)
            outputStream.writeBytes("settings put global adb_enabled $onInt\n")
            outputStream.flush()
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            su.waitFor()
        } catch (e: IOException) {
            throw Exception(e)
        } catch (e: InterruptedException) {
            throw Exception(e)
        }
    }
}