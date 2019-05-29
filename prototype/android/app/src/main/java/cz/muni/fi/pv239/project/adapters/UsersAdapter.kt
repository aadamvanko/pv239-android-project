package cz.muni.fi.pv239.project.adapters

import android.graphics.Color
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
        val user = users[position]
        name.text = user.name
        if (user.selected) {
            view.setBackgroundColor(Color.parseColor("#009688"))
            name.setTextColor(Color.WHITE)
        }
        return view
    }

}
