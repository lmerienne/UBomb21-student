/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.go.character;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Grid;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Movable;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.view.ImageResource;
import fr.ubx.poo.ubomb.view.SpritePlayer;


public class Player extends GameObject implements Movable {

    private Direction direction;
    private boolean moveRequested = false;
    private int lives,bombcapacity;
    private int bombrange=1;


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
    public void takeDoor(int gotoLevel) {}
    public void takeKey() {}
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



    }


    public boolean isWinner() {
        Player player = this.game.getPlayer();
        Position pos = player.getPosition();
        Grid grid = game.getGrid();
        Decor princess= grid.get(pos);
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

}
