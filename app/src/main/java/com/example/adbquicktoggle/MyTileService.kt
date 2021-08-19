package com.example.adbquicktoggle

import android.content.Context
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import java.io.DataOutputStream
import java.io.IOException
import java.lang.Exception

class MyTileService : TileService() {

    private val context: Context = this

    override fun onStartListening() {
        super.onStartListening()
        setState()
        Log.v("Basic", "in startListening after state set")
    }

    private fun setState(){
        if (adbActive()){
            qsTile.state = Tile.STATE_ACTIVE
            Log.v("Basic", "in if setstate => state_active")
        }
        else if (!adbActive()) {
            qsTile.state = Tile.STATE_INACTIVE
            Log.v("Basic", "in if else setstate => state_active")

        }
    }

    override fun onTileAdded() {
        super.onTileAdded()
        //check adb state
        //update state
        qsTile.state = Tile.STATE_ACTIVE

        //update looks
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val adbAct = adbActive()
        var toastmsg = "Has to be dynamic."
        Log.d("ADB_onClick", "state: $adbAct")
        if (adbAct){
            //TODO("Change adb state.")
            qsTile.state = Tile.STATE_INACTIVE //Don't do this in if else but after
            setAdb(false)
        }
        else if(!adbAct){
            qsTile.state = Tile.STATE_ACTIVE //Don't do this in if else but after
            setAdb(true)
        }
        //TODO("Print the adb state as a toast message.")
        /*val toast = */ Toast.makeText(context, toastmsg, Toast.LENGTH_SHORT).show()
        //toast.show()
        qsTile.updateTile()
    }

    private fun adbActive(): Boolean {
        val state = Settings.Global.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0)
        //Log.d("ADB_STATE", "state: $state")
        return state == 1
    }

    private fun setAdb(on: Boolean){
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