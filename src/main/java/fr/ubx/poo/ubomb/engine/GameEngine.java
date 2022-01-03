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
import fr.ubx.poo.ubomb.go.decor.bonus.Bonus;
import fr.ubx.poo.ubomb.go.decor.bonus.Key;
import fr.ubx.poo.ubomb.view.*;
import fr.ubx.poo.ubomb.game.Grid;
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

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.io.IOException;


public final class GameEngine {

    public static int level;
    private static AnimationTimer gameLoop;
    private final String windowTitle;
    private final Game game;
    private Player player;
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();
    public static LinkedList<Grid> grides = new LinkedList<>();
    private final Stage stage;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;
    private Grid grid;


    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.stage = stage;
        this.windowTitle = windowTitle;
        this.game = game;
        this.grid = game.getGrid();
        this.player = game.getPlayer();
        grides.add(game.getGrid());
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
                if (player.newLevel()) {
                    try {
                        player.takeDoor(player.whichLvl());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sprites.clear();
                    initialize();


                    int playerLives = player.getLives();

                    Position playerPosition = posDoor();
                    int bag = player.getBombcapacity();
                    player = new Player(game, playerPosition, playerLives,bag);
                    sprites.add(new SpritePlayer(layer, player));
                    statusBar.update(game,player);
                        }

                // Do actions
                try {
                    update(now);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                createNewBombs(now);
                checkCollision(now);
                checkExplosions();

                // Graphic update
                cleanupSprites();
                render();
                statusBar.update(game,player);
                for (int i=0; i<game.getGrid().getWidth();i++){
                    for (int j=0;j<game.getGrid().getHeight();j++){
                        Position pos2 = new Position(i,j);
                        Decor monster=game.getGrid().get(pos2);

                        if( monster instanceof Monster){
                            try {
                                ((Monster) monster).update(game, now);
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                        }

                    }}

            }


        };
    }

    private Position posDoor(){
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                Position pos = new Position(i,j);
                if(grid.get(pos) instanceof DoorOpenPrev){
                    return pos;
                }
            }
        }
        return new Position(0,0);
    }

    private void checkExplosions(){}
    private boolean checkBoom(Position pos) {
        if(game.getGrid().get(pos) instanceof Key){
            return false;
        }
        if (game.getGrid().get(pos)instanceof Box || game.getGrid().get(pos)instanceof Monster|| game.getGrid().get(pos)instanceof Bonus||game.getGrid().get(pos)==null){
            return true;
        }
        return false;
    }
    private boolean checkPropa(Position pos){
        if(game.getGrid().get(pos) instanceof Tree||game.getGrid().get(pos) instanceof Stone ){
            return false;
        }
        return true;
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
    private boolean checkBox(Position pos){
        System.out.println("box="+game.getGrid().get(pos));
        if(game.getGrid().get(pos)instanceof Box){
            return false;
        }
        return true;
    }
    private void checkPlayerExplosion(Position pos){
        if (pos.equals(player.getPosition())){
            player.explode();
        }
    }
    private void createNewBombs(long now) {
    }
    private void checkCollision(long now) {
    }
    private void bombExplosion(Position pos, Sprite ex){

        if (checkBoom(pos) && game.inside(pos)) {
            checkPlayerExplosion(pos);

            sprites.add(ex);

            if (checkDestruction(pos) ) {
                Decor decor = game.getGrid().get(pos);
                if (decor instanceof Monster){
                    decor.remove();

                }
                decor.remove();
                game.getGrid().remove(pos);
            }
            ex.remove();
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {

                        public void run() {
                            ex.getGameObject().remove();


                        }
                    },
                    1000
            );
        }

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
                                                                            player.moreBomb();
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
    private void bombPlaced(Position pos) {
        boolean propa1=true;
        boolean propa4=true;
        boolean propa3=true;
        boolean propa2=true;
        GameObject ex_on = new Explosion(new Position(pos.getX(), pos.getY()));
        Sprite ex_5 =SpriteFactory.create(layer,ex_on);
        bombExplosion(ex_on.getPosition(),ex_5);
        for (int i = 1; i <= player.getBombRange(); i++) {

            //Sprite bombe=SpriteFactory.create(layer, bomb);
            //sprites.add(bombe);

            GameObject ex_under = new Explosion(new Position(pos.getX(), pos.getY() - i));
            Sprite ex_1 = SpriteFactory.create(layer, ex_under);
            if(propa1){
                propa1=checkPropa(ex_under.getPosition());
            }
            if(propa1){
                propa1= checkBox(ex_under.getPosition());
                bombExplosion(ex_under.getPosition(), ex_1);
            }


            //sprites.get(sprites.size() - 1).remove();
            GameObject ex_up = new Explosion(new Position(pos.getX(), pos.getY() + i));
            Sprite ex_2 = SpriteFactory.create(layer, ex_up);
            if(propa2){
                propa2=checkPropa(ex_up.getPosition());
            }
            if(propa2){
                propa2= checkBox(ex_up.getPosition());
                bombExplosion(ex_up.getPosition(), ex_2);

            }


            ////////////////////////////////////////////////////////////////////////////////////////////
            GameObject ex_left = new Explosion(new Position(pos.getX() - i, pos.getY()));
            Sprite ex_3 = SpriteFactory.create(layer, ex_left);
            if(propa3){
                propa3=checkPropa(ex_left.getPosition());
            }
            if(propa3){
                propa3= checkBox(ex_left.getPosition());
                bombExplosion(ex_left.getPosition(), ex_3);

            }


            ///////////////////////////////////////////////////////////////////////////////////////////
            GameObject ex_right = new Explosion(new Position(pos.getX() + i, pos.getY()));
            Sprite ex_4 = SpriteFactory.create(layer, ex_right);
            if(propa4){
                propa4=checkPropa(ex_right.getPosition());
            }
            if(propa4){
                propa4= checkBox(ex_right.getPosition());
                bombExplosion(ex_right.getPosition(), ex_4);
            }


            ///////////////////////////////////////////////////////////////////////////////////////////

        }
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
        }else if (input.isBomb()) {
            if (player.getBombcapacity() > 0) {
                System.out.println();
                player.lessBomb();
                bombDestruction(3);
            }
        }else if (input.isKey() && player.getKey()==1) {
            if(game.getGrid().get(player.getDirection().nextPosition(player.getPosition())) instanceof DoorClose) {
                System.out.println(game.getGrid().get(player.getDirection().nextPosition(player.getPosition())));
                openDoor();

            }
            input.clear();
        }

        input.clear();
    }

    public void openDoor(){
        System.out.println("Door Opened");

        GameObject doornext = new DoorOpenNext(player.getDirection().nextPosition(player.getPosition()));
        Sprite door_next = new Sprite(layer, ImageResource.getDoorNext(), doornext);
        sprites.add(door_next);
        player.openDoor();
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


    private void update(long now) throws IOException {
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
