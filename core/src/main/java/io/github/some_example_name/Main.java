package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Random;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;
    private Popup popup;

    private Window window = null;

    private int score = 0;
    private int highScore = 0;
    class Popup {
        float timeLeft;
        TextButton button;
        Window window;

        Popup(){
            this.timeLeft = 7;
            this.window = new Window("Example screen", skin, "border");
            window.defaults().pad(4f);

            window.add("You'll never catch me >:)").row();
            this.button = new TextButton(((int)this.timeLeft)+"", skin);
            button.pad(8f);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(final ChangeEvent event, final Actor actor) {
                    timeLeft = 4;
                    Random rand = new Random();
                    window.setPosition(rand.nextInt((int)window.getWidth()*8),rand.nextInt((int)window.getHeight()*8));
                    score++;
                }
            });
            window.add(button);
            window.pack();
            // We round the window position to avoid awkward half-pixel artifacts.
            // Casting using (int) would also work.
            Random rand = new Random();
            window.setPosition(rand.nextInt((int)window.getWidth()*8),rand.nextInt((int)window.getHeight()*8));
            window.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)));
            stage.addActor(window);
        }

        boolean update(float dt){
            timeLeft -= dt;
            this.button.setText(((int)timeLeft)+"");
            if(timeLeft <= 0){
              window.remove();
              return true;
            }
            return false;
        }
    }

    @Override
    public void create() {
        stage = new Stage(new FitViewport(640, 480));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        popup = new Popup();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        boolean gameOver = popup.update(Gdx.graphics.getDeltaTime());
        if (gameOver && this.window == null){
            this.window = new Window("  Game Over", skin, "border");
            window.defaults().pad(4f);
            window.add("    You died >:D    ").row();
            window.add("you closed: " + score +" windows");
            if (score > highScore){
                highScore = score;
            }
            window.add("Highscore: " + highScore);
            TextButton button = new TextButton("restart", skin);
            button.pad(8f);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(final ChangeEvent event, final Actor actor) {
                    window.remove();
                    window = null;
                    popup = new Popup();
                    score = 0;
                }
            });
            window.add(button);
            window.pack();
            // We round the window position to avoid awkward half-pixel artifacts.
            // Casting using (int) would also work.
            window.setPosition(MathUtils.roundPositive(stage.getWidth() / 2f - window.getWidth() / 2f),
                MathUtils.roundPositive(stage.getHeight() / 2f - window.getHeight() / 2f));
            window.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)));
            stage.addActor(window);
        }

        //loop over windows
        //check for time under 1
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
