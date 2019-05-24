package cz.muni.fi.pv239.project.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import cz.muni.fi.pv239.project.db.DatabaseImpl
import cz.muni.fi.pv239.project.db.DbWorkerThread
import cz.muni.fi.pv239.project.entities.User
import processing.test.project.R

class ActivityNewUser : AppCompatActivity() {

    private var database: DatabaseImpl? = null
    private lateinit var dbWorkerThread: DbWorkerThread

    private lateinit var buttonOK: Button
    private lateinit var editTextName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        editTextName = findViewById(R.id.edit_text_new_user_name)

        buttonOK = findViewById(R.id.button_new_user_ok)
        buttonOK.setOnClickListener {
            val newUserName = editTextName.text.toString()
            val task = Runnable {
                val users = database?.userDAO()?.getAll()
                if (users != null) {
                    val user = users.find { it.name == newUserName }
                    if (user == null && newUserName != "") {
                        database?.userDAO()?.insert(User(null, newUserName, false))
                    }
                }
            }
            dbWorkerThread.postTask(task)
            finish()
        }

        dbWorkerThread = DbWorkerThread("dbWorkerThread")
        dbWorkerThread.start()
        Thread.sleep(500)

        database = DatabaseImpl.getInstance(this)
    }

    override fun onDestroy() {
        dbWorkerThread.quit()
        super.onDestroy()
    }

}
