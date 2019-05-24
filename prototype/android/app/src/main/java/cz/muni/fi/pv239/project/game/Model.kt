package cz.muni.fi.pv239.project.game

import cz.muni.fi.pv239.project.game.entities.Ball
import cz.muni.fi.pv239.project.game.entities.BorderPart
import java.util.*

class Model {
    var score: Long = 0
    var gameSpeed = 1
    var multiplier: Long = 1
    var borders: ArrayList<BorderPart> = ArrayList()
    lateinit var ball: Ball
    var realFrameCount = 0
}