package cz.muni.fi.pv239.project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cz.muni.fi.pv239.project.entities.HighScoreItem
import processing.test.project.R

class HighScoreItemsAdapter(val highScoreItems: List<HighScoreItem>, val layoutInflater: LayoutInflater) : BaseAdapter() {

    override fun getItem(position: Int): Any {
        return highScoreItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return highScoreItems.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = layoutInflater.inflate(R.layout.high_score_item, null)

        val positionNumber: TextView = view.findViewById(R.id.text_view_position_number)
        val user: TextView = view.findViewById(R.id.text_view_user)
        val score: TextView = view.findViewById(R.id.text_view_score)
        positionNumber.text = (position + 1).toString() + "."
        user.text = highScoreItems[position].username
        score.text = highScoreItems[position].score.toString()
        return view
    }

}
