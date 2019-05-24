package cz.muni.fi.pv239.project.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.FrameLayout
import cz.muni.fi.pv239.project.db.DatabaseImpl
import cz.muni.fi.pv239.project.db.DbWorkerThread
import cz.muni.fi.pv239.project.entities.HighScoreItem
import cz.muni.fi.pv239.project.game.GameApplet
import cz.muni.fi.pv239.project.game.GameFragment
import cz.muni.fi.pv239.project.game.GameFragmentInterface
import processing.android.CompatUtils
import processing.core.PApplet

class ActivityPlay : AppCompatActivity(), GameFragmentInterface {

    private lateinit var sketch: PApplet

    private var database: DatabaseImpl? = null
    private lateinit var dbWorkerThread: DbWorkerThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val frame = FrameLayout(this)
        frame.id = CompatUtils.getUniqueViewId()
        setContentView(frame, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))
        /*sketch = GameApplet()
        val fragment = PFragment(sketch)
        fragment.setView(frame, this)
        */
        sketch = GameApplet(this)
        val frag = GameFragment()
        frag.sketch = sketch
        frag.setView(frame, this)

        dbWorkerThread = DbWorkerThread("dbWorkerThread")
        dbWorkerThread.start()
        Thread.sleep(200)

        database = DatabaseImpl.getInstance(this)
    }

    override fun onGameOver(score: Long) {
        val task = Runnable {
            val users = database?.userDAO()?.getAll()
            if (users != null) {
                val selectedUser = users.find { it.selected }
                if (selectedUser != null) {
                    val highScoreItems = database?.highScoreItemDAO()?.getAll()
                    if (highScoreItems != null) {
                        if (highScoreItems.size == 10) {
                            val minScoreItem = highScoreItems.sortedByDescending { it.score }.last()
                            database?.highScoreItemDAO()?.delete(minScoreItem)
                        }
                        database?.highScoreItemDAO()?.insert(HighScoreItem(null, selectedUser.name, score))
                    }
                }
            }
        }
        dbWorkerThread.postTask(task)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (sketch != null) {
            sketch.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onNewIntent(intent: Intent) {
        if (sketch != null) {
            sketch.onNewIntent(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (sketch != null) {
            sketch.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        if (sketch != null) {
            sketch.onBackPressed()
        }
        finish()
    }

    override fun onDestroy() {
        dbWorkerThread.quit()
        super.onDestroy()
    }


}