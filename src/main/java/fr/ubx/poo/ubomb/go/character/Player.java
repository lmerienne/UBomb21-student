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
import fr.ubx.poo.ubomb.go.decor.Decor;
import fr.ubx.poo.ubomb.go.decor.Monster;
import fr.ubx.poo.ubomb.go.decor.Princess;


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
        if (pos.getX()<0 || pos.getX() > grid.getWidth() -1 || pos.getY() < 0 || pos.getY() > grid.getHeight()-1){
            return false;
        }
        return true;
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
