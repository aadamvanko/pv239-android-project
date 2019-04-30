

enum BorderPartType {
    NORMAL, 
    DEADLY,
    MULTIPLIER
}

class Settings {
    // sizes
    public static final int SCREEN_WIDTH = 300;
    public static final int SCREEN_HEIGHT = (int)(SCREEN_WIDTH * (16/(float)9));
    public static final int BORDER_PART_WIDTH = (int)(SCREEN_WIDTH * 0.05);
    public static final int BORDER_PART_HEIGHT = (int)(SCREEN_HEIGHT * 0.1);
    public static final int BALL_SIZE = (int)(SCREEN_WIDTH * 0.08);
    
    // mechanics
    public static final float GRAVITY = 0.4;
    public static final float MAX_VELOCITY = 10.0;
    public static final float JUMP_VELOCITY = 8.0;
    public static final int MAX_BORDERS_SPEED = 10;
    public static final float BALL_VELOCITY_X = 4.0;

    // colors
    public static final int BACKGROUND_COLOR = #FFFFFF;
    public static final int NORMAL_BLOCK_COLOR = #30343F;
    public static final int DEADLY_BLOCK_COLOR = #C32929;
    public static final int MULTIPLIER_BLOCK_COLOR = #fff654;
    public static final int BALL_COLOR = #1A4489;
    public static final int TEXT_SCORE_COLOR = #e6e1f2;
}

class BorderPart {
    public int x;
    public int y;
    public BorderPartType type;
}

class Ball {
    public float x;
    public float y;
    public float velx;
    public float vely;
}

class Enemy {
    public float x;
    public float y;
    public float rotateSpeed;
}

long score = 0;
int gameSpeed = 1;
long multiplier = 1;
ArrayList<BorderPart> borders = new ArrayList();
Ball ball;
Enemy enemy;
boolean paused = true;

// PREPARE

BorderPartType randomBorderPartType() {
    int value = (int)random(15);
    if (value < 10) {
        return BorderPartType.NORMAL;
    } else if (value < 14) {
        return BorderPartType.DEADLY;
    } else {
        if (frameCount % 2 == 0) {
            return BorderPartType.MULTIPLIER;
        } else {
            return BorderPartType.NORMAL;
        }
    }
}

void prepareModel() {

    int numberOfParts = Settings.SCREEN_HEIGHT / Settings.BORDER_PART_HEIGHT + 2;
    for (int i = 0; i < numberOfParts; i++) {
        BorderPart borderPart = new BorderPart();
        borderPart.x = 0;
        borderPart.y = i * Settings.BORDER_PART_HEIGHT;
        borderPart.type = BorderPartType.NORMAL;
        borders.add(borderPart);

        borderPart = new BorderPart();
        borderPart.x = Settings.SCREEN_WIDTH - Settings.BORDER_PART_WIDTH;
        borderPart.y = i * Settings.BORDER_PART_HEIGHT;
        borderPart.type = BorderPartType.NORMAL;
        borders.add(borderPart);
    }
    
    ball = new Ball();
    ball.x = Settings.SCREEN_WIDTH / 2;
    ball.y = Settings.SCREEN_HEIGHT / 2;
    ball.velx = Settings.BALL_VELOCITY_X;
    ball.vely = 1.0;
    
    enemy = new Enemy();
    enemy.x = Settings.SCREEN_WIDTH / 2;
    enemy.y = -15;
    enemy.rotateSpeed = 1.f;
}

// UPDATE

void moveBorders() { 
    for (BorderPart borderPart : borders) {
        borderPart.y += gameSpeed;
    }

    for (BorderPart borderPart : borders) {
        if (borderPart.y >= Settings.SCREEN_HEIGHT) {
            BorderPart withLowestY = borderPart;
            for (BorderPart bp : borders) {
                if (bp.y < withLowestY.y && bp.x == borderPart.x) {
                    withLowestY = bp;
                }
            }
            borderPart.y = withLowestY.y - Settings.BORDER_PART_HEIGHT;
            borderPart.type = randomBorderPartType();
        }
    }
}

boolean isCollision(BorderPart borderPart, Ball ball) {
    if (ball.x + Settings.BALL_SIZE < borderPart.x || ball.x - Settings.BALL_SIZE > borderPart.x + Settings.BORDER_PART_WIDTH) {
        return false;
    }
    
    if (ball.y < borderPart.y || ball.y > borderPart.y + Settings.BORDER_PART_HEIGHT) {
        return false;
    }
    
    return true;
}

void borderTouch() {
    for (BorderPart borderPart : borders) {
        if (isCollision(borderPart, ball)) {
            if (borderPart.type == BorderPartType.MULTIPLIER) {
                multiplier *= 2;
            }
        }
    }
}

void moveBall() {
    ball.x += ball.velx;
    
    if (ball.x - Settings.BALL_SIZE / 2 < Settings.BORDER_PART_WIDTH) {
        ball.velx *= -1;
        ball.x = Settings.BORDER_PART_WIDTH + Settings.BALL_SIZE;
        borderTouch();
    }
    
    if (ball.x + Settings.BALL_SIZE / 2 >= Settings.SCREEN_WIDTH - Settings.BORDER_PART_WIDTH) {
        ball.velx *= -1;
        ball.x = Settings.SCREEN_WIDTH - Settings.BORDER_PART_WIDTH - Settings.BALL_SIZE;
        borderTouch();
    }
    
    ball.y += ball.vely;
    ball.vely += Settings.GRAVITY;
    ball.vely = min(ball.vely, Settings.MAX_VELOCITY);
    
    if (ball.y + Settings.BALL_SIZE >= Settings.SCREEN_HEIGHT) {
        ball.vely = -Settings.JUMP_VELOCITY;
    }
    
    if (ball.y - Settings.BALL_SIZE < 0) {
        ball.vely = 2.5;
    }
}

void moveEnemy() {
    enemy.y += 1;
    if (enemy.y > Settings.SCREEN_HEIGHT + 150) {
        enemy.y = -150;
    }
}

void update() {
    gameSpeed = min(frameCount / 500 + 1, Settings.MAX_BORDERS_SPEED);
    score += gameSpeed * multiplier;
    
    moveBorders();
    moveBall();
    //moveEnemy();
}

// RENDER

void drawBorderPart(BorderPart borderPart) {
    noStroke();
    if (borderPart.type == BorderPartType.NORMAL) {
        fill(Settings.NORMAL_BLOCK_COLOR);
    } else if (borderPart.type == BorderPartType.DEADLY) {
        fill(Settings.DEADLY_BLOCK_COLOR);
    } else if (borderPart.type == BorderPartType.MULTIPLIER) {
        fill(Settings.MULTIPLIER_BLOCK_COLOR);
    }

    rect(borderPart.x, borderPart.y, Settings.BORDER_PART_WIDTH, Settings.BORDER_PART_HEIGHT);
}

void drawBorders() {
    for (BorderPart borderPart : borders) {
        drawBorderPart(borderPart);
    }
}

void drawEnemy(Enemy enemy) {
    fill(Settings.DEADLY_BLOCK_COLOR);
    for(int i = 0 ; i < 360; i+=360/min(3, gameSpeed)){
        float x = enemy.x + cos(radians(i + frameCount))*100;
        float y = enemy.y + sin(radians(i + frameCount))*100;
        ellipse(x,y, 15, 15);
    }
}

void drawBall(Ball ball) {
    noStroke();
    fill(Settings.BALL_COLOR);
    ellipse(ball.x, ball.y, Settings.BALL_SIZE, Settings.BALL_SIZE);
}

void drawScore() {
    fill(Settings.TEXT_SCORE_COLOR);
    text("x" + String.valueOf(multiplier), Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2 - 30);
    text(String.valueOf(score), Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);
}

void render() {
    fill(Settings.BACKGROUND_COLOR, 100);
    rect(0, 0, width, height);
    //background(Settings.BACKGROUND_COLOR);

    drawScore();
    drawBorders();
    //drawEnemy(enemy);
    drawBall(ball);
}


// LOOP

void setup() {
    //fullScreen(P3D);
    size(300, 533, P3D);
    //colorMode(HSB, 360, 100, 100, 100);
    smooth(4);
    frameRate(60);
    background(Settings.BACKGROUND_COLOR);
    
    textSize(30);
    textAlign(CENTER, CENTER);

    prepareModel();
}

void draw() {
    if (!paused) {
        update();
    }
    render();

    //println(frameRate);
}

void mouseClicked() {
    if (paused) {
        paused = false;
    }
    ball.vely = -Settings.JUMP_VELOCITY;
}

void keyPressed() {
    if (key == 'c' || key == 'C') {
    }
}
