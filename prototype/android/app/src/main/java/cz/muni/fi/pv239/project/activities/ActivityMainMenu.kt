package cz.muni.fi.pv239.project.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import cz.muni.fi.pv239.project.db.DatabaseImpl
import cz.muni.fi.pv239.project.db.DbWorker
import cz.muni.fi.pv239.project.entities.User
import processing.test.project.R

class ActivityMainMenu : AppCompatActivity() {

    private var database: DatabaseImpl? = null
    private lateinit var dbWorker: DbWorker

    private lateinit var buttonPlay: Button
    private lateinit var buttonHighScores: Button
    private lateinit var buttonChangeUser: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        buttonPlay = findViewById(R.id.button_play)
        buttonHighScores = findViewById(R.id.button_high_scores)
        buttonChangeUser = findViewById(R.id.button_change_user)

        buttonPlay.setOnClickListener {
            var intent = Intent(this, ActivityPlay::class.java)
            this.startActivity(intent)
        }

        buttonHighScores.setOnClickListener {
            var intent = Intent(this, ActivityHighScores::class.java)
            this.startActivity(intent)
        }

        buttonChangeUser.setOnClickListener {
            var intent = Intent(this, ActivityChangeUser::class.java)
            this.startActivity(intent)
        }

        dbWorker = DbWorker()

        database = DatabaseImpl.getInstance(this)
        addDefaultUser()
    }

    private fun addDefaultUser() {
        val task = Runnable {
            val users = database?.userDAO()?.getAll()
            if (users == null || users.isEmpty()) {
                database?.userDAO()?.insert(User(null, "Unknown", true))
            }
        }
        dbWorker.postTask(task)
    }

    override fun onDestroy() {
        DatabaseImpl.destroyInstance()
        dbWorker.destroy()
        super.onDestroy()
    }

}