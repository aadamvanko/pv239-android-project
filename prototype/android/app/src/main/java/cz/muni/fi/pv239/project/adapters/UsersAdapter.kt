package cz.muni.fi.pv239.project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cz.muni.fi.pv239.project.entities.User
import processing.test.project.R

class UsersAdapter(val users: List<User>, val layoutInflater: LayoutInflater) : BaseAdapter() {

    override fun getItem(position: Int): Any {
        return users[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return users.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = layoutInflater.inflate(R.layout.user_item, null)

        val name: TextView = view.findViewById(R.id.text_view_name)
        val selected: TextView = view.findViewById(R.id.text_view_selected)
        name.text = users[position].name
        selected.text = if (users[position].selected) "SELECTED" else ""
        return view
    }

}
