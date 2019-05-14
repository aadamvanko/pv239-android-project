enum GameState {
    READY,
    RUNNING,
    FINISHED
}

enum BorderPartType {
    NORMAL, 
    DEADLY,
    MULTIPLIER
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

class Settings {
    // sizes
    public int SCREEN_WIDTH = 1080;
    public int SCREEN_HEIGHT = (int)(SCREEN_WIDTH * (16/(float)9));
    public int BORDER_PART_WIDTH = (int)(SCREEN_WIDTH * 0.05);
    public int BORDER_PART_HEIGHT = (int)(SCREEN_HEIGHT * 0.1);
    public int BALL_SIZE = (int)(SCREEN_WIDTH * 0.08);
    
    // mechanics
    public float GRAVITY = 0.4;
    public float MAX_VELOCITY = 10.0;
    public float JUMP_VELOCITY = 8.0;
    public int MAX_BORDERS_SPEED = 10;
    public float BALL_VELOCITY_X = 4.0;

    // colors
    public int BACKGROUND_COLOR = #FFFFFF;
    public int NORMAL_BLOCK_COLOR = #30343F;
    public int DEADLY_BLOCK_COLOR = #C32929;
    public int MULTIPLIER_BLOCK_COLOR = #fff654;
    public int BALL_COLOR = #1A4489;
    public int TEXT_SCORE_COLOR = #e6e1f2;
}

class Model {
    public long score = 0;
    public int gameSpeed = 1;
    public long multiplier = 1;
    public ArrayList<BorderPart> borders = new ArrayList();
    public Ball ball;
    public int realFrameCount = 0;
}

class Renderer {
    private Settings settings;
    private Model model;
    
    public Renderer(Settings settings, Model model) {
        this.settings = settings;
        this.model = model;
    }
    
    public void render() {
        fill(settings.BACKGROUND_COLOR, 100);
        rect(0, 0, width, height);
        //background(settings.BACKGROUND_COLOR);
    
        drawScore(model);
        drawBorders(model.borders);
        //drawEnemy(enemy);
        drawBall(model.ball);
    }
    
    void drawBorderPart(BorderPart borderPart) {
        noStroke();
        if (borderPart.type == BorderPartType.NORMAL) {
            fill(settings.NORMAL_BLOCK_COLOR);
        } else if (borderPart.type == BorderPartType.DEADLY) {
            fill(settings.DEADLY_BLOCK_COLOR);
        } else if (borderPart.type == BorderPartType.MULTIPLIER) {
            fill(settings.MULTIPLIER_BLOCK_COLOR);
        }
    
        rect(borderPart.x, borderPart.y, settings.BORDER_PART_WIDTH, settings.BORDER_PART_HEIGHT);
    }
    
    void drawBorders(ArrayList<BorderPart> borders) {
        for (BorderPart borderPart : borders) {
            drawBorderPart(borderPart);
        }
    }
    
    void drawBall(Ball ball) {
        noStroke();
        fill(settings.BALL_COLOR);
        ellipse(ball.x, ball.y, settings.BALL_SIZE, settings.BALL_SIZE);
    }
    
    void drawScore(Model model) {
        fill(settings.TEXT_SCORE_COLOR);
        float fontSize = settings.SCREEN_WIDTH / 10;
        textSize(fontSize);
        text("x" + String.valueOf(model.multiplier), settings.SCREEN_WIDTH / 2, settings.SCREEN_HEIGHT / 2 - fontSize / 2);
        text(String.valueOf(model.score), settings.SCREEN_WIDTH / 2, settings.SCREEN_HEIGHT / 2 + fontSize / 2);
    }
}

class Game
{
    private boolean isMouseDown = false;
    private GameState state = GameState.READY;
    private Settings settings;
    private Model model;
    private Renderer renderer;
    
    public Game(int width)
    {
        preparesettings(width);
        prepareModel();
        prepareRenderer();
    }
    
    public void run() {
        state = GameState.RUNNING;
    }
    
    public GameState getState() {
        return state;
    }
    
    public void setState(GameState state) {
        this.state = state;
    }
    
    private void update() {
        handleInput();
        
        model.gameSpeed = min(model.realFrameCount / 500 + 1, settings.MAX_BORDERS_SPEED);
        model.score += model.gameSpeed * model.multiplier;
        model.realFrameCount++;

        moveBorders(model.borders);
        moveBall(model.ball, model.borders);
    }
    
    private void handleInput() {
        if (mousePressed) {
            if (!isMouseDown) {
                isMouseDown = true;
                model.ball.vely = -settings.JUMP_VELOCITY;
            }
        } else {
            isMouseDown = false;
        }
    }
    
    private void render() {
        renderer.render();
    }
    
    private void preparesettings(int screenWidth) {
        settings = new Settings();
        // 300 533
        // object sizes
        settings.SCREEN_WIDTH = screenWidth;
        settings.SCREEN_HEIGHT = (int)(settings.SCREEN_WIDTH * (16/(float)9));
        settings.BORDER_PART_WIDTH = (int)(settings.SCREEN_WIDTH * 0.05);
        settings.BORDER_PART_HEIGHT = (int)(settings.SCREEN_HEIGHT * 0.1);
        settings.BALL_SIZE = (int)(settings.SCREEN_WIDTH * 0.08);
        
        // mechanics
        settings.GRAVITY = settings.SCREEN_WIDTH / 750.0;
        settings.MAX_VELOCITY = settings.SCREEN_WIDTH / 30.0;
        settings.JUMP_VELOCITY = settings.SCREEN_WIDTH / 37.5;
        settings.MAX_BORDERS_SPEED = settings.SCREEN_WIDTH / 30;
        settings.BALL_VELOCITY_X = settings.SCREEN_WIDTH / 75.0;
        
        // colors
        settings.BACKGROUND_COLOR = #FFFFFF;
        settings.NORMAL_BLOCK_COLOR = #30343F;
        settings.DEADLY_BLOCK_COLOR = #C32929;
        settings.MULTIPLIER_BLOCK_COLOR = #fff654;
        settings.BALL_COLOR = #1A4489;
        settings.TEXT_SCORE_COLOR = #e6e1f2;
    }
    
    private void prepareModel() {
        model = new Model();
        
        int numberOfParts = settings.SCREEN_HEIGHT / settings.BORDER_PART_HEIGHT + 2;
        for (int i = 0; i < numberOfParts; i++) {
            BorderPart borderPart = new BorderPart();
            borderPart.x = 0;
            borderPart.y = i * settings.BORDER_PART_HEIGHT;
            borderPart.type = BorderPartType.NORMAL;
            model.borders.add(borderPart);
    
            borderPart = new BorderPart();
            borderPart.x = settings.SCREEN_WIDTH - settings.BORDER_PART_WIDTH;
            borderPart.y = i * settings.BORDER_PART_HEIGHT;
            borderPart.type = BorderPartType.NORMAL;
            model.borders.add(borderPart);
        }
        
        Ball ball = new Ball();
        ball.x = settings.SCREEN_WIDTH / 2;
        ball.y = settings.SCREEN_HEIGHT / 2;
        ball.velx = settings.BALL_VELOCITY_X;
        ball.vely = 1.0;
        model.ball = ball;
    }
    
    private void prepareRenderer() {
        renderer = new Renderer(settings, model);
    }
    
    private BorderPartType randomBorderPartType() {
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

    void moveBorders(ArrayList<BorderPart> borders) { 
        for (BorderPart borderPart : borders) {
            borderPart.y += model.gameSpeed;
        }
    
        for (BorderPart borderPart : borders) {
            if (borderPart.y >= settings.SCREEN_HEIGHT) {
                BorderPart withLowestY = borderPart;
                for (BorderPart bp : borders) {
                    if (bp.y < withLowestY.y && bp.x == borderPart.x) {
                        withLowestY = bp;
                    }
                }
                borderPart.y = withLowestY.y - settings.BORDER_PART_HEIGHT;
                borderPart.type = randomBorderPartType();
            }
        }
    }
    
    boolean isCollision(BorderPart borderPart, Ball ball) {
        if (ball.x + settings.BALL_SIZE < borderPart.x || ball.x - settings.BALL_SIZE > borderPart.x + settings.BORDER_PART_WIDTH) {
            return false;
        }
        
        if (ball.y < borderPart.y || ball.y > borderPart.y + settings.BORDER_PART_HEIGHT) {
            return false;
        }
        
        return true;
    }
    
    void borderTouch(ArrayList<BorderPart> borders) {
        for (BorderPart borderPart : borders) {
            if (isCollision(borderPart, model.ball)) {
                if (borderPart.type == BorderPartType.MULTIPLIER) {
                    model.multiplier *= 2;
                } else if (borderPart.type == BorderPartType.DEADLY) {
                    state = GameState.FINISHED;
                }
            }
        }
    }
    
    void moveBall(Ball ball, ArrayList<BorderPart> borders) {
        ball.x += ball.velx;
        
        if (ball.x - settings.BALL_SIZE / 2 < settings.BORDER_PART_WIDTH) {
            ball.velx *= -1;
            ball.x = settings.BORDER_PART_WIDTH + settings.BALL_SIZE;
            borderTouch(borders);
        }
        
        if (ball.x + settings.BALL_SIZE / 2 >= settings.SCREEN_WIDTH - settings.BORDER_PART_WIDTH) {
            ball.velx *= -1;
            ball.x = settings.SCREEN_WIDTH - settings.BORDER_PART_WIDTH - settings.BALL_SIZE;
            borderTouch(borders);
        }
        
        ball.y += ball.vely;
        ball.vely += settings.GRAVITY;
        ball.vely = min(ball.vely, settings.MAX_VELOCITY);
        
        if (ball.y >= settings.SCREEN_HEIGHT) {
            state = GameState.FINISHED;
        }
        
        if (ball.y - settings.BALL_SIZE / 2 < 0) {
            ball.vely = settings.JUMP_VELOCITY / 3;
        }
    }

}

// LOOP

Game game;

void setup() {
    //fullScreen(P3D);
    size(300, 533, P3D);
    //size(1080, 1920, P3D);
    smooth(4);
    frameRate(60);
    
    textSize(30);
    textAlign(CENTER, CENTER);
    
    game = new Game(width);
}

void draw() {
    if (game.getState() == GameState.RUNNING) {
        game.update();
    }
    game.render();
}

void mousePressed() {
    if (game.getState() == GameState.READY) {
        game.run();
    }
}
