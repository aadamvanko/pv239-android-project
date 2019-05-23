package processing.test.prototype

import android.util.Half.toFloat
import processing.core.*
import processing.data.*
import processing.event.*
import processing.opengl.*

import java.util.HashMap
import java.util.ArrayList
import java.io.File
import java.io.BufferedReader
import java.io.PrintWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.IOException

class Prototype : PApplet() {
    // LOOP
    internal lateinit var game: Game

    internal enum class GameState {
        READY,
        RUNNING,
        FINISHED
    }

    internal enum class BorderPartType {
        NORMAL,
        DEADLY,
        MULTIPLIER
    }

    internal inner class BorderPart {
        var x: Int = 0
        var y: Int = 0
        var type = BorderPartType.NORMAL;
    }

    internal inner class Ball {
        var x: Float = 0f
        var y: Float = 0f
        var velx: Float = 0f
        var vely: Float = 0f
    }

    internal inner class Settings {
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

    internal inner class Model {
        var score: Long = 0
        var gameSpeed = 1
        var multiplier: Long = 1
        var borders: ArrayList<BorderPart> =

                ArrayList()

        lateinit var ball: Ball
        var realFrameCount = 0
    }

    internal inner class Renderer(settings: Settings, model: Model) {
        private val settings: Settings
        private val model: Model

        init {
            this.settings = settings
            this.model = model
        }

        fun render() {
            fill(settings.BACKGROUND_COLOR.toInt(), 100.0f)
            rect(0f, 0f, width.toFloat(), height.toFloat())
            //background(settings.BACKGROUND_COLOR);
            drawScore(model)
            drawBorders(model.borders)
            //drawEnemy(enemy);
            drawBall(model.ball)
        }

        fun drawBorderPart(borderPart: BorderPart) {
            noStroke()
            if (borderPart.type == BorderPartType.NORMAL) {
                fill(settings.NORMAL_BLOCK_COLOR)
            } else if (borderPart.type == BorderPartType.DEADLY) {
                fill(settings.DEADLY_BLOCK_COLOR)
            } else if (borderPart.type == BorderPartType.MULTIPLIER) {
                fill(settings.MULTIPLIER_BLOCK_COLOR)
            }
            rect(borderPart.x.toFloat(), borderPart.y.toFloat(), settings.BORDER_PART_WIDTH.toFloat(), settings.BORDER_PART_HEIGHT.toFloat())
        }

        fun drawBorders(borders: ArrayList<BorderPart>) {
            for (borderPart in borders) {
                drawBorderPart(borderPart)
            }
        }

        fun drawBall(ball: Ball) {
            noStroke()
            fill(settings.BALL_COLOR)
            ellipse(ball.x, ball.y, settings.BALL_SIZE.toFloat(), settings.BALL_SIZE.toFloat())
        }

        fun drawScore(model: Model) {
            fill(settings.TEXT_SCORE_COLOR)
            val fontSize = settings.SCREEN_WIDTH / 10f
            textSize(fontSize)
            text("x" + (model.multiplier).toString(), settings.SCREEN_WIDTH / 2f, settings.SCREEN_HEIGHT / 2 - fontSize / 2)
            text((model.score).toString(), settings.SCREEN_WIDTH / 2f, settings.SCREEN_HEIGHT / 2 + fontSize / 2)
        }
    }

    internal inner

    class Game(width: Int) {
        private var isMouseDown = false
        var state = GameState.READY
        private lateinit var settings: Settings
        private lateinit var model: Model
        private lateinit var renderer: Renderer

        init {
            preparesettings(width)
            prepareModel()
            prepareRenderer()
        }

        fun run() {
            state = GameState.RUNNING
        }

        fun update() {
            handleInput()
            model.gameSpeed = min(model.realFrameCount / 500 + 1, settings.MAX_BORDERS_SPEED)
            model.score += model.gameSpeed * model.multiplier
            model.realFrameCount++
            moveBorders(model.borders)
            moveBall(model.ball, model.borders)
        }

        private fun handleInput() {
            if (mousePressed) {
                if (!isMouseDown) {
                    isMouseDown = true
                    model.ball.vely = -settings.JUMP_VELOCITY
                }
            } else {
                isMouseDown = false
            }
        }

        fun render() {
            renderer.render()
        }

        private fun preparesettings(screenWidth: Int) {
            settings = Settings()
            // 300 533
            // object sizes
            settings.SCREEN_WIDTH = screenWidth
            settings.SCREEN_HEIGHT = (settings.SCREEN_WIDTH * (16 / 9.toFloat())).toInt()
            settings.BORDER_PART_WIDTH = (settings.SCREEN_WIDTH * 0.05f).toInt()
            settings.BORDER_PART_HEIGHT = (settings.SCREEN_HEIGHT * 0.1f).toInt()
            settings.BALL_SIZE = (settings.SCREEN_WIDTH * 0.08f).toInt()
            // mechanics
            settings.GRAVITY = settings.SCREEN_WIDTH / 750.0f
            settings.MAX_VELOCITY = settings.SCREEN_WIDTH / 30.0f
            settings.JUMP_VELOCITY = settings.SCREEN_WIDTH / 37.5f
            settings.MAX_BORDERS_SPEED = settings.SCREEN_WIDTH / 30
            settings.BALL_VELOCITY_X = settings.SCREEN_WIDTH / 75.0f
            // colors
            settings.BACKGROUND_COLOR = -0x1
            settings.NORMAL_BLOCK_COLOR = -0xcfcbc1
            settings.DEADLY_BLOCK_COLOR = -0x3cd6d7
            settings.MULTIPLIER_BLOCK_COLOR = -0x9ac
            settings.BALL_COLOR = -0xe5bb77
            settings.TEXT_SCORE_COLOR = -0x191e0e
        }

        private fun prepareModel() {
            model = Model()
            val numberOfParts = settings.SCREEN_HEIGHT / settings.BORDER_PART_HEIGHT + 2
            for (i in 0..numberOfParts) {
                val leftBorderPart = BorderPart()
                leftBorderPart.x = 0
                leftBorderPart.y = i * settings.BORDER_PART_HEIGHT
                leftBorderPart.type = BorderPartType.NORMAL
                model.borders.add(leftBorderPart)

                val rightBorderPart = BorderPart()
                rightBorderPart.x = settings.SCREEN_WIDTH - settings.BORDER_PART_WIDTH
                rightBorderPart.y = i * settings.BORDER_PART_HEIGHT
                rightBorderPart.type = BorderPartType.NORMAL
                model.borders.add(rightBorderPart)
            }
            val ball = Ball()
            ball.x = (settings.SCREEN_WIDTH / 2).toFloat()
            ball.y = (settings.SCREEN_HEIGHT / 2).toFloat()
            ball.velx = settings.BALL_VELOCITY_X
            ball.vely = 1.0f
            model.ball = ball
        }

        private fun prepareRenderer() {
            renderer = Renderer(settings, model)
        }

        private fun randomBorderPartType(): BorderPartType {
            val value = random(15f).toInt()
            if (value < 10) {
                return BorderPartType.NORMAL
            } else if (value < 14) {
                return BorderPartType.DEADLY
            } else {
                if (frameCount % 2 === 0) {
                    return BorderPartType.MULTIPLIER
                } else {
                    return BorderPartType.NORMAL
                }
            }
        }

        fun moveBorders(borders: ArrayList<BorderPart>) {
            for (borderPart in borders) {
                borderPart.y += model.gameSpeed
            }
            for (borderPart in borders) {
                if (borderPart.y >= settings.SCREEN_HEIGHT) {
                    var withLowestY = borderPart
                    for (bp in borders) {
                        if (bp.y < withLowestY.y && bp.x == borderPart.x) {
                            withLowestY = bp
                        }
                    }
                    borderPart.y = withLowestY.y - settings.BORDER_PART_HEIGHT
                    borderPart.type = randomBorderPartType()
                }
            }
        }

        fun isCollision(borderPart: BorderPart, ball: Ball): Boolean {
            if (ball.x + settings.BALL_SIZE < borderPart.x || ball.x - settings.BALL_SIZE > borderPart.x + settings.BORDER_PART_WIDTH) {
                return false
            }
            if (ball.y < borderPart.y || ball.y > borderPart.y + settings.BORDER_PART_HEIGHT) {
                return false
            }
            return true
        }

        fun borderTouch(borders: ArrayList<BorderPart>) {
            for (borderPart in borders) {
                if (isCollision(borderPart, model.ball)) {
                    if (borderPart.type == BorderPartType.MULTIPLIER) {
                        model.multiplier *= 2
                    } else if (borderPart.type == BorderPartType.DEADLY) {
                        state = GameState.FINISHED
                    }
                }
            }
        }

        fun moveBall(ball: Ball, borders: ArrayList<BorderPart>) {
            ball.x += ball.velx
            if (ball.x - settings.BALL_SIZE / 2 < settings.BORDER_PART_WIDTH) {
                ball.velx *= -1f
                ball.x = (settings.BORDER_PART_WIDTH + settings.BALL_SIZE).toFloat()
                borderTouch(borders)
            }
            if (ball.x + settings.BALL_SIZE / 2 >= settings.SCREEN_WIDTH - settings.BORDER_PART_WIDTH) {
                ball.velx *= -1f
                ball.x = (settings.SCREEN_WIDTH - settings.BORDER_PART_WIDTH - settings.BALL_SIZE).toFloat()
                borderTouch(borders)
            }
            ball.y += ball.vely
            ball.vely += settings.GRAVITY
            ball.vely = min(ball.vely, settings.MAX_VELOCITY)
            if (ball.y >= settings.SCREEN_HEIGHT) {
                state = GameState.FINISHED
            }
            if (ball.y - settings.BALL_SIZE / 2 < 0) {
                ball.vely = settings.JUMP_VELOCITY / 3
            }
        }
    }

    override fun setup() {
        //size(300, 533, P3D);
        //size(1080, 1920, P3D);
        frameRate(60f)
        textSize(30f)
        textAlign(CENTER, CENTER)
        game = Game(width)
    }

    override fun draw() {
        if (game.state == GameState.RUNNING) {
            game.update()
        }
        game.render()
    }

    override fun mousePressed() {
        if (game.state == GameState.READY) {
            game.run()
        }
    }

    override fun settings() {
        fullScreen(P3D)
        smooth(4)
    }
}