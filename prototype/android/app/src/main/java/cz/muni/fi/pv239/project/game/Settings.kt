package cz.muni.fi.pv239.project.game

class Settings {
    // sizes
    var SCREEN_WIDTH = 1080
    var SCREEN_HEIGHT = (SCREEN_WIDTH * (16 / 9f)).toInt()

    var BORDER_PART_WIDTH = (SCREEN_WIDTH * 0.05f).toInt()
    var BORDER_PART_HEIGHT = (SCREEN_HEIGHT * 0.1f).toInt()
    var BALL_SIZE = (SCREEN_WIDTH * 0.08f).toInt()
    // mechanics
    var GRAVITY = 0.4f
    var MAX_VELOCITY = 10.0f
    var JUMP_VELOCITY = 8.0f
    var MAX_BORDERS_SPEED = 10
    var BALL_VELOCITY_X = 4.0f
    // colors
    var BACKGROUND_COLOR = -0x1
    var NORMAL_BLOCK_COLOR = -0xcfcbc1
    var DEADLY_BLOCK_COLOR = -0x3cd6d7
    var MULTIPLIER_BLOCK_COLOR = -0x9ac
    var BALL_COLOR = -0xe5bb77
    var TEXT_SCORE_COLOR = -0x191e0e
}