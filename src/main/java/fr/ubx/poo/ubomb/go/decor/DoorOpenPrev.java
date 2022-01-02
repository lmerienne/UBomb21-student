package fr.ubx.poo.ubomb.go.decor;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;

public class DoorOpenPrev extends Decor{
    public DoorOpenPrev(Position position){super(position);}
    @Override
    public boolean isWalkable(Player player){
        return true;
    }
}
