package cz.muni.fi.pv239.project.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import processing.android.PFragment

interface GameFragmentInterface {
    fun onGameOver(score: Long)
}

class GameFragment : PFragment() {

    private var viewSaved: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (viewSaved != null) {
            if (viewSaved?.parent as ViewGroup != null) {
                (viewSaved as ViewGroup).removeView(viewSaved)
            }
            return viewSaved
        }

        viewSaved = super.onCreateView(inflater, container, savedInstanceState)
        return viewSaved
    }

}