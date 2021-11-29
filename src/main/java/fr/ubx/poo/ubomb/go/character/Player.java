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
import fr.ubx.poo.ubomb.go.decor.bonus.Bomb_range_inc;
import fr.ubx.poo.ubomb.view.Sprite;
import fr.ubx.poo.ubomb.view.SpriteFactory;

import java.util.LinkedList;


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
        Position pos= direction.nextPosition(super.getPosition());
        System.out.println("pos="+pos);
        Grid grid = game.getGrid();
        Decor element= grid.get(pos);
        if(element instanceof Tree || element instanceof Stone || element instanceof Box){
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
        Player player = this.game.getPlayer();
        Position pos = player.getPosition();

    }

    // Example of methods to define by the player
    public void takeDoor(int gotoLevel) {}
    public void takeKey() {}
    public void takeBonus(){}


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
}
