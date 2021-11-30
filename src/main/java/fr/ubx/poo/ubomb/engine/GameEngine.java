/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.engine;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.character.Player;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.decor.bonus.Bomb_range_inc;
import fr.ubx.poo.ubomb.go.decor.bonus.Bonus;
import fr.ubx.poo.ubomb.go.decor.bonus.Key;
import fr.ubx.poo.ubomb.view.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final String windowTitle;
    private final Game game;
    private final Player player;
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();
    private final Stage stage;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;


    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.stage = stage;
        this.windowTitle = windowTitle;
        this.game = game;
        this.player = game.getPlayer();
        initialize();
        buildAndSetGameLoop();
    }

    private void initialize() {
        Group root = new Group();
        layer = new Pane();

        int height = game.getGrid().getHeight();
        int width = game.getGrid().getWidth();
        int sceneWidth = width * Sprite.size;
        int sceneHeight = height * Sprite.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight, game);

        // Create sprites
        for (Decor decor : game.getGrid().values()) {
            sprites.add(SpriteFactory.create(layer, decor));
            decor.setModified(true);
        }
        sprites.add(new SpritePlayer(layer, player));
    }

    void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Check keyboard actions
                processInput(now);

                // Do actions
                update(now);
                createNewBombs(now);
                checkCollision(now);
                checkExplosions();

                // Graphic update
                cleanupSprites();
                render();
                statusBar.update(game);
            }
        };
    }
    private void checkExplosions(){}
    private boolean checkBoom(Position pos) {
        if (game.getGrid().get(pos)instanceof Box || game.getGrid().get(pos)instanceof Monster|| game.getGrid().get(pos)instanceof Bonus||game.getGrid().get(pos)==null){
            return true;
        }
        return false;
    }
    private boolean checkDestruction(Position pos){
        if(game.getGrid().get(pos) instanceof Key){
            return false;
        }
        if (game.getGrid().get(pos) instanceof Box||game.getGrid().get(pos) instanceof Bonus||game.getGrid().get(pos) instanceof Monster){
            return true;
        }
        return false;
    }
    private void createNewBombs(long now) {
    }

    private void checkCollision(long now) {
    }
    private void bombDestruction(int i){
        GameObject bomb_0= new Bomb_0(player.getPosition());
        System.out.println(sprites);
        GameObject bomb_1= new Bomb_1(player.getPosition());
        GameObject bomb_2= new Bomb_2(player.getPosition());
        GameObject bomb_3= new Bomb_3(player.getPosition());
        Sprite bombe = new Sprite(layer, ImageResource.getBomb(i), bomb_3);
        sprites.add(bombe);
        bombe.remove();
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                        @Override
                        public void run() {
                            // your code here

                            bomb_3.remove();

                            Sprite bombe=new Sprite(layer,ImageResource.getBomb(i-1),bomb_2);
                            sprites.add(bombe);
                            bombe.remove();
                            new java.util.Timer().schedule(
                                    new java.util.TimerTask() {
                                        @Override
                                        public void run() {
                                            // your code here

                                            bomb_2.remove();
                                            Sprite bombe=new Sprite(layer,ImageResource.getBomb(i-2),bomb_1);
                                            sprites.add(bombe);
                                            bombe.remove();
                                            new java.util.Timer().schedule(
                                                    new java.util.TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            // your code here

                                                            bomb_1.remove();
                                                            Sprite bombe=new Sprite(layer,ImageResource.getBomb(i-3),bomb_0);
                                                            sprites.add(bombe);
                                                            bombe.remove();
                                                            new java.util.Timer().schedule(
                                                                    new java.util.TimerTask() {
                                                                        @Override
                                                                        public void run() {
                                                                            // your code here

                                                                            bomb_0.remove();
                                                                            bombPlaced(bomb_0.getPosition());
                                                                        }
                                                                    },
                                                                    1000
                                                            );

                                                        }
                                                    },
                                                    1000
                                            );
                                        }
                                    },
                                    1000
                            );

                        }
                    },
                    1000
            );
        }
    private void bombPlaced(Position pos){

            for (int i = 1; i <= 1; i++) {

                //Sprite bombe=SpriteFactory.create(layer, bomb);
                //sprites.add(bombe);

                GameObject ex_under = new Explosion(new Position(pos.getX(), pos.getY() - i));
                if (checkBoom(ex_under.getPosition()) && game.inside(ex_under.getPosition())) {
                    Sprite ex = SpriteFactory.create(layer, ex_under);
                    sprites.add(ex);
                    if (checkDestruction(ex_under.getPosition())) {
                        Decor decor = game.getGrid().get(ex_under.getPosition());
                        decor.remove();
                        game.getGrid().remove(ex_under.getPosition());
                    }

                    ex.remove();
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    // your code here

                                    ex.getGameObject().remove();
                                }
                            },
                            1000
                    );
                    //sprites.remwqwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww&&&w&&ove(sprites.size()-1);
                }
                sprites.get(sprites.size() - 1).remove();
                GameObject ex_up = new Explosion(new Position(pos.getX(), pos.getY() + i));
                if (checkBoom(ex_up.getPosition()) && game.inside(ex_up.getPosition())) {
                    Sprite ex = SpriteFactory.create(layer, ex_up);
                    sprites.add(ex);
                    if (checkDestruction(ex_up.getPosition())) {
                        Decor decor = game.getGrid().get(ex_up.getPosition());
                        decor.remove();
                        game.getGrid().remove(ex_up.getPosition());

                    }
                    // sprites.remove(sprites.size()-1);
                    ex.remove();
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    // your code here
                                    ex.getGameObject().remove();
                                }
                            },
                            1000
                    );
                }
                GameObject ex_left = new Explosion(new Position(pos.getX() - i, pos.getY()));
                if (checkBoom(ex_left.getPosition()) && game.inside(ex_left.getPosition())) {
                    Sprite ex = SpriteFactory.create(layer, ex_left);
                    sprites.add(ex);
                    if (checkDestruction(ex_left.getPosition())) {
                        Decor decor = game.getGrid().get(ex_left.getPosition());
                        decor.remove();
                        game.getGrid().remove(ex_left.getPosition());

                    }
                    // sprites.remove(sprites.size()-1);
                    ex.remove();
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    // your code here
                                    ex.getGameObject().remove();
                                }
                            },
                            1000
                    );
                }
                GameObject ex_right = new Explosion(new Position(pos.getX() + i, pos.getY()));
                if (checkBoom(ex_right.getPosition()) && game.inside(ex_right.getPosition())) {
                    Sprite ex = SpriteFactory.create(layer, ex_right);
                    sprites.add(ex);
                    if (checkDestruction(ex_right.getPosition())) {
                        Decor decor = game.getGrid().get(ex_right.getPosition());
                        decor.remove();
                        game.getGrid().remove(ex_right.getPosition());

                    }
                    //sprites.remove(sprites.size()-1);
                    ex.remove();
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    // your code here
                                    ex.getGameObject().remove();
                                }
                            },
                            1000
                    );
                }
                GameObject ex_on = new Explosion(new Position(pos.getX(), pos.getY()));
                if (checkBoom(ex_on.getPosition()) && game.inside(ex_on.getPosition())) {
                    Sprite ex = SpriteFactory.create(layer, ex_on);
                    sprites.add(ex);
                    if (checkDestruction(ex_on.getPosition())) {
                        Decor decor = game.getGrid().get(ex_on.getPosition());
                        decor.remove();
                        game.getGrid().remove(ex_on.getPosition());

                    }
                    //sprites.remove(sprites.size()-1);
                    ex.remove();
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    // your code here
                                    ex.getGameObject().remove();
                                }
                            },
                            1000
                    );
                }
            }

        /*sprites.add(SpriteFactory.create(layer, ex_under));
        sprites.add(SpriteFactory.create(layer, ex_up));
        sprites.add(SpriteFactory.create(layer, ex_left));

*/
        }

    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        } else if (input.isMoveDown()) {
            player.requestMove(Direction.DOWN);
        } else if (input.isMoveLeft()) {
            player.requestMove(Direction.LEFT);
        } else if (input.isMoveRight()) {
            player.requestMove(Direction.RIGHT);
        } else if (input.isMoveUp()) {
            player.requestMove(Direction.UP);
        }else if (input.isBomb()){
            bombDestruction(3);
            input.clear();
        }

        input.clear();
    }

    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }


    private void update(long now) {
        player.update(now);

        if (player.getLives() == 0) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }

        if (player.isWinner()) {
            gameLoop.stop();
            showMessage("Gagné", Color.BLUE);
        }
    }

    public void cleanupSprites() {
        sprites.forEach(sprite -> {
            if (sprite.getGameObject().isDeleted()) {
                game.getGrid().remove(sprite.getPosition());
                cleanUpSprites.add(sprite);
            }
        });
        cleanUpSprites.forEach(Sprite::remove);
        sprites.removeAll(cleanUpSprites);
        cleanUpSprites.clear();
    }

    private void render() {
        sprites.forEach(Sprite::render);
    }

    public void start() {
        gameLoop.start();
    }
}
