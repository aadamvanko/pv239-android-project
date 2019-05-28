package cz.muni.fi.pv239.project.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
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
    private var userName: String = ""
    private lateinit var loadedUsers: List<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user)

        usersListView = findViewById(R.id.list_view_users)
        usersListView.setOnItemClickListener { parent, view, position, id ->
            val entry = parent.adapter.getItem(position) as User
            userName = entry.name
            selectUser()
            loadUsersFromDb()
        }

        usersListView.setOnItemLongClickListener { parent, view, position, id ->
            val entry = parent.adapter.getItem(position) as User
            userName = entry.name
            false
        }

        registerForContextMenu(usersListView)

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

    override fun onResume() {
        super.onResume()

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
            loadedUsers = users!!
        }
        dbWorkerThread.postTask(task)
    }

    private fun showDataInUI(users: List<User>) {
        usersListView.adapter = UsersAdapter(users, layoutInflater)
    }

    override fun onDestroy() {
        dbWorkerThread.quit()
        super.onDestroy()
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (menu != null && v != null && chosenUserNotSelected()) {
            menu.add(0, 0, 0, "Delete")
        }
    }

    private fun chosenUserNotSelected(): Boolean {
        val selectedUser = loadedUsers.find({ it.selected })
        if (selectedUser != null) {
            return selectedUser.name != userName
        }
        return true
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if (item.itemId == 0) {
                deleteUser()
                loadUsersFromDb()
            }
        }
        return true
    }

    private fun selectUser() {
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
    }

    private fun deleteUser() {
        val task = Runnable {
            val user = loadUserFromDb()
            if (user != null && !user.selected) {
                database?.userDAO()?.delete(user)
            }
        }
        dbWorkerThread.postTask(task)
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

}
