package cz.muni.fi.pv239.project.activities

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import cz.muni.fi.pv239.project.adapters.HighScoreItemsAdapter
import cz.muni.fi.pv239.project.db.DatabaseImpl
import cz.muni.fi.pv239.project.db.DbWorker
import cz.muni.fi.pv239.project.entities.HighScoreItem
import processing.test.project.R

class ActivityHighScores : AppCompatActivity() {

    private var database: DatabaseImpl? = null
    private lateinit var dbWorker: DbWorker
    private val mUiHandler = Handler()

    private lateinit var highScoreItemsListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        highScoreItemsListView = findViewById(R.id.list_view_items)

        dbWorker = DbWorker()
        database = DatabaseImpl.getInstance(this)
        loadHighScoreItemsFromDb()
    }

    private fun insertHighScoreItemIntoDb(highScoreItem: HighScoreItem) {
        val task = Runnable { database?.highScoreItemDAO()?.insert(highScoreItem) }
        dbWorker.postTask(task)
    }

    private fun loadHighScoreItemsFromDb() {
        val task = Runnable {
            val highScoreItems = database?.highScoreItemDAO()?.getAll()
            mUiHandler.post {
                if (highScoreItems == null) {
                    throw Exception("No existing high score table")
                } else {
                    showDataInUI(highScoreItems)
                }
            }
        }
        dbWorker.postTask(task)
    }

    private fun showDataInUI(highScoreItems: List<HighScoreItem>) {
        val highestToLowestScore = highScoreItems.sortedByDescending { it.score }
        highScoreItemsListView.adapter = HighScoreItemsAdapter(highestToLowestScore, layoutInflater)
    }

    override fun onDestroy() {
        DatabaseImpl.destroyInstance()
        dbWorker.destroy()
        super.onDestroy()
    }

}
