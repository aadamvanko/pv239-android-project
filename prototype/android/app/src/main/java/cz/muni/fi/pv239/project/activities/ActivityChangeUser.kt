package cz.muni.fi.pv239.project.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import cz.muni.fi.pv239.project.adapters.UsersAdapter
import cz.muni.fi.pv239.project.db.DatabaseImpl
import cz.muni.fi.pv239.project.db.DbWorkerThread
import cz.muni.fi.pv239.project.entities.User
import processing.test.project.R

class ActivityChangeUser : AppCompatActivity() {

    private var database: DatabaseImpl? = null
    private lateinit var dbWorkerThread: DbWorkerThread
    private val uiHandler = Handler()

    private lateinit var usersListView: ListView
    private lateinit var buttonNewUser: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user)

        usersListView = findViewById(R.id.list_view_users)
        usersListView.setOnItemClickListener { parent, view, position, id ->
            val entry = parent.adapter.getItem(position) as User
            parent.setSelection(position)

            var intent = Intent(this, ActivityUserOperations::class.java)
            intent.putExtra("userName", entry.name)
            this.startActivity(intent)
        }

        buttonNewUser = findViewById(R.id.button_new_user)
        buttonNewUser.setOnClickListener {
            var intent = Intent(this, ActivityNewUser::class.java)
            this.startActivity(intent)
        }

        dbWorkerThread = DbWorkerThread("dbWorkerThread")
        dbWorkerThread.start()
        Thread.sleep(500)

        database = DatabaseImpl.getInstance(this)
        loadUsersFromDb()
    }

    private fun loadUsersFromDb() {
        val task = Runnable {
            val users = database?.userDAO()?.getAll()
            uiHandler.post {
                if (users == null) {
                    throw Exception("No existing users table")
                } else {
                    showDataInUI(users)
                }
            }
        }
        dbWorkerThread.postTask(task)
    }

    private fun showDataInUI(users: List<User>) {
        usersListView.adapter = UsersAdapter(users, layoutInflater)
    }

    override fun onResume() {
        super.onResume()

        loadUsersFromDb()
    }

    override fun onDestroy() {
        dbWorkerThread.quit()
        super.onDestroy()
    }

}
