/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.go.decor;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;

public class Box extends Decor {
    public Box(Position position) {
        super(position);
    }
    public boolean moveBox(Game game, Direction dir){
        Position pos_before=getPosition();
        Position pos_after=dir.nextPosition(getPosition());
        if(game.getGrid().get(pos_after) !=null || !game.inside(pos_after)){
            return false;
        }
        this.setPosition(pos_after);
        game.getGrid().remove(pos_before);
        game.getGrid().set(pos_after,this);
        return true;
    }
}
