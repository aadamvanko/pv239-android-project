package cz.muni.fi.pv239.project.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import cz.muni.fi.pv239.project.db.DatabaseImpl
import cz.muni.fi.pv239.project.db.DbWorkerThread
import cz.muni.fi.pv239.project.entities.User
import processing.test.project.R

class ActivityUserOperations : AppCompatActivity() {

    private var database: DatabaseImpl? = null
    private lateinit var dbWorkerThread: DbWorkerThread
    private var userName: String = "Unknown"

    private lateinit var buttonSelect: Button
    private lateinit var buttonDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_operations)

        userName = intent.getStringExtra("userName")
        println(userName)

        buttonSelect = findViewById(R.id.button_select_user)
        buttonDelete = findViewById(R.id.button_delete_user)

        buttonSelect.setOnClickListener {
            buttonSelect.isEnabled = false
            val task = Runnable {
                val user = loadUserFromDb()
                if (user != null) {
                    val users = database?.userDAO()?.getAll()
                    if (users != null) {
                        for (fetchedUser in users) {
                            if (fetchedUser.name != user.name && fetchedUser.selected) {
                                fetchedUser.selected = false
                                database?.userDAO()?.update(fetchedUser)
                            }
                        }
                    }
                    user.selected = true
                    database?.userDAO()?.update(user)
                }
            }
            dbWorkerThread.postTask(task)
            finish()
        }

        buttonDelete.setOnClickListener {
            buttonDelete.isEnabled = false
            val task = Runnable {
                val user = loadUserFromDb()
                if (user != null && !user.selected) {
                    database?.userDAO()?.delete(user)
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

    private fun loadUserFromDb(): User {
        val users = database?.userDAO()?.getAll()
        for (fetchedUser in users!!) {
            if (fetchedUser.name == userName) {
                return fetchedUser
            }
        }
        throw Exception("User with given name not found, internal error between activities")
    }

    override fun onDestroy() {
        dbWorkerThread.quit()
        super.onDestroy()
    }
}
