/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.go.character;

import fr.ubx.poo.ubomb.engine.GameEngine;
import fr.ubx.poo.ubomb.game.*;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Movable;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.view.ImageResource;
import fr.ubx.poo.ubomb.view.Sprite;
import fr.ubx.poo.ubomb.view.SpriteFactory;
import fr.ubx.poo.ubomb.view.SpritePlayer;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Player extends GameObject implements Movable {

    private Direction direction;
    private boolean moveRequested = false;
    private int lives;

    public Player(Game game, Position position, int lives) {
        super(game, position);
        this.direction = Direction.DOWN;
        this.lives = lives;
    }

    public int getLives() {
        return lives;
    }

    public Direction getDirection() {
        return direction;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    public final boolean canMove(Direction direction) {
        Player player = this.game.getPlayer();
        Position pos= direction.nextPosition(super.getPosition());
        System.out.println("pos="+pos);
        Grid grid = game.getGrid();
        if (pos.getX()<0 || pos.getX() > grid.getWidth() -1 || pos.getY() < 0 || pos.getY() > grid.getHeight()-1){
            return false;
        }Decor element= grid.get(pos);

        /*if(element instanceof Tree || element instanceof Stone || element instanceof Box){
            return false;
        }*/
        if(element == null || element instanceof Princess || element instanceof Monster || element instanceof DoorClose){
            return true;
        }
        return element.isWalkable(player);

    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;
    }

    public void doMove(Direction direction) {
        // Check if we need to pick something up
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
        Grid grid = game.getGrid();
        Decor monstre = grid.get(nextPos);
        System.out.println("playerget"+game.getGrid().get(getPosition()));
        if(monstre instanceof Monster){

            this.lives -= 1;
        }
    }

    @Override
    public boolean isWalkable(Player player) {
        return false;
    }

    @Override
    public void explode() {
    }

    // Example of methods to define by the player
    public Grid takeDoor(int gotoLevel) throws IOException {
        String path = getClass().getResource("/sample").getFile();
        InputStream input = new FileInputStream(new File(path, "config.properties"));
        Properties prop = new Properties();
        prop.load(input);
        String prefix = prop.getProperty("prefix");
        Grid grid;

        System.out.println(GameEngine.grides);
        System.out.println("dans le else");
        if (gotoLevel == 1){
            if (GameEngine.grides.size() == GameEngine.level) {
                System.out.println("Level changed");
                GridRepo gridRepo = new GridRepoFile(this.game);
                grid = gridRepo.load(GameEngine.level + 1, path + "/" + prefix);
                GameEngine.grides.add(grid);
            }else {
                grid = GameEngine.grides.listIterator().next();
            }
        }
        else{
            //grid = GameEngine.grides.listIterator().previous();
            GameEngine.level -= 1;
            grid = GameEngine.grides.listIterator(GameEngine.level + 1).previous();
        }
        return grid;
    }

    public void takeKey() {}
    public void takeBonus(){}



    public boolean isWinner() {
        Grid grid = game.getGrid();
        Decor princess= grid.get(this.getPosition());
        if(princess instanceof Princess ) {
            return true;
        }
        return false;
    }

    public boolean ski(){
        Grid grid = game.getGrid();
        Decor door = grid.get(this.getPosition());
        //System.out.println(door);
        if(door instanceof DoorOpenNext || door instanceof DoorOpenPrev){
            System.out.println("Change of level...");
            return true;
        }
        return false;
    }
    public int sku(){
        Grid grid = game.getGrid();
        Decor door = grid.get(this.getPosition());
        if (door instanceof DoorOpenNext){
            System.out.println(1);
            GameEngine.level += 1;
            return 1;
        }
        System.out.println(-1);
        return -1;
    }
    public void openDoor(){
        Position pos = this.getDirection().nextPosition(this.getPosition());
        Decor skiski = new DoorOpenNext(pos);
        this.game.getGrid().remove(pos);
        this.game.getGrid().set(pos, skiski);
    }
}
