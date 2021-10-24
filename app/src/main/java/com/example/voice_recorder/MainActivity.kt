package com.example.voice_recorder

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var state: Boolean = false
    private var myAudioRecorder: MediaRecorder? = null
    private var output: String? = null
    private var button_start_recording: Button? = null
    private var button_stop_recording: Button? = null
    private var button_pause_recording: Button? = null
    private var permissionToRecordAccepted = false
    private var permissionToWriteAccepted = false
    private val permissions =
        arrayOf("android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val requestCode = 200
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
        button_start_recording = findViewById<View>(R.id.button_start_recording) as Button
        button_stop_recording = findViewById<View>(R.id.button_stop_recording) as Button
        button_start_recording!!.setOnClickListener(this)
        button_stop_recording!!.setOnClickListener(this)
        button_stop_recording!!.isEnabled = false
        output = Environment.getExternalStorageDirectory().absolutePath + "/myrecording.mp3"
        val file = File(Environment.getExternalStorageDirectory(), "myrecording.mp3")
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                val getpermission = Intent();
                getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;
                startActivity(getpermission);
            }
        }
        file.createNewFile()
        myAudioRecorder = MediaRecorder()
        myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        myAudioRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        myAudioRecorder!!.setOutputFile(file.absolutePath)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_start_recording -> start()
            R.id.button_stop_recording -> stop()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            200 -> {
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                permissionToWriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
            }
        }
        if (!permissionToRecordAccepted) super@MainActivity.finish()
        if (!permissionToWriteAccepted) super@MainActivity.finish()
    }

    private fun start() {
        try {
            myAudioRecorder!!.prepare()
            myAudioRecorder!!.start()
            state = true
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        button_start_recording!!.isEnabled = false
        button_stop_recording!!.isEnabled = true
        Toast.makeText(applicationContext, "Recording started", Toast.LENGTH_SHORT).show()
    }

    private fun stop() {
        try {
            if (state)
                Toast.makeText(applicationContext, "Try to stop", Toast.LENGTH_SHORT).show()
            myAudioRecorder!!.stop()
            myAudioRecorder!!.release()
            state = false
        }catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        myAudioRecorder = null
        button_stop_recording!!.isEnabled = false
        button_pause_recording!!.isEnabled = true
        Toast.makeText(applicationContext, "Audio recorded successfully", Toast.LENGTH_SHORT).show()
    }
}
