/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.go.character;

import fr.ubx.poo.ubomb.engine.GameEngine;
import fr.ubx.poo.ubomb.game.*;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Movable;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.view.ImageResource;
import fr.ubx.poo.ubomb.view.SpritePlayer;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;



public class Player extends GameObject implements Movable {

    private Direction direction;
    private boolean moveRequested = false;
    private int lives,bombcapacity;
    private int bombrange=1;
    private int key=0;


    public Player(Game game, Position position, int lives, int bombcapacity) {
        super(game, position);
        this.direction = Direction.DOWN;
        this.lives = lives;
        this.bombcapacity=bombcapacity;
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
        Position pos= direction.nextPosition(super.getPosition());
        System.out.println("pos="+pos);
        Grid grid = game.getGrid();
        Decor element= grid.get(pos);
        takeBonus();
        if(element instanceof Box){
            Box box= (Box)element;
            return box.moveBox(game,direction);
        }
        if(element instanceof Tree || element instanceof Stone){
            return false;
        }
                return game.inside(pos);
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
        this.lives-=1;

    }

    // Example of methods to define by the player

    public void takeDoor(int gotoLevel) throws IOException {
        String path = getClass().getResource("/sample").getFile();
        InputStream input = new FileInputStream(new File(path, "config.properties"));
        Properties prop = new Properties();
        prop.load(input);
        String prefix = prop.getProperty("prefix");
        Grid grid;

        System.out.println(GameEngine.grides);
        if (gotoLevel == 1){
            System.out.println("truc");
            if (GameEngine.grides.size() == GameEngine.level) {
                System.out.println("Level changed");
                GridRepo gridRepo = new GridRepoFile(this.game);
                grid = gridRepo.load(GameEngine.level + 1, path + "/" + prefix);
                GameEngine.grides.add(grid);
            }else {
                grid = GameEngine.grides.listIterator().next();
            }
            int velocity = Integer.parseInt(prop.getProperty("monsterVelocity")) ;
            prop.setProperty("monsterVelocity", String.valueOf(velocity+5));
        }
        else{
            System.out.println("pas la");
            GameEngine.level -= 1;
            System.out.println(GameEngine.grides.listIterator(GameEngine.level + 1).previous());
            //GridRepo gridRepo = new GridRepoFile(this.game);
            //grid = gridRepo.load(GameEngine.level + 1, path + "/" + prefix);
            grid = GameEngine.grides.listIterator(GameEngine.level + 1).previous();


            int velocity = Integer.parseInt(prop.getProperty("monsterVelocity")) ;
            prop.setProperty("monsterVelocity", String.valueOf(velocity-5));

        }
        System.out.println(GameEngine.grides);
        game.setGrid(grid);
    }

    public void takeKey() {
        Decor key=game.getGrid().get(getPosition());
        if (key instanceof Key){
            this.key+=1;
            key.remove();
        }
    }
    public void takeBonus(){
        Decor decor = (game.getGrid().get(getPosition()));
        if ( decor instanceof Hearth){
            this.lives+=1;
            decor.remove();
        }
        if (decor instanceof BombInc){
            this.bombcapacity+=1;
            decor.remove();
        }
        if (decor instanceof BombDec && bombcapacity>0){
            this.bombcapacity-=1;
            decor.remove();
        }
        if (decor instanceof RangeInc){
            this.bombrange+=1;
            decor.remove();
        }
        if (decor instanceof RangeDec && bombrange>1){
            this.bombrange-=1;
            decor.remove();
        }
        if(decor instanceof Key){
            this.key+=1;
            decor.remove();;
        }



    }


    public boolean isWinner() {
        Grid grid = game.getGrid();
        Decor princess= grid.get(getPosition());
        if(princess instanceof Princess ) {
            return true;
        }
        return false;
    }
 public int getBombcapacity(){
        return bombcapacity;
    }
    public void moreBomb(){
        bombcapacity+=1;
    }
    public void lessBomb(){
        bombcapacity-=1;
    }
    public int getBombRange(){return bombrange;}
    public void moreRange(){bombrange+=1;}
    public void lessRange(){bombrange-=1;}

    public void openDoor(){
        Position pos = this.getDirection().nextPosition(this.getPosition());
        Decor skiski = new DoorOpenNext(pos);
        this.game.getGrid().remove(pos);
        this.game.getGrid().set(pos, skiski);
        this.key-=1;
    }

    public boolean newLevel(){
        Grid grid = game.getGrid();
        Decor door = grid.get(this.getPosition());
        if(door instanceof DoorOpenNext || door instanceof DoorOpenPrev){
            System.out.println("Change of level...");
            return true;
        }
        return false;
    }

    public int whichLvl(){
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
    public int getKey(){return key;}
}

