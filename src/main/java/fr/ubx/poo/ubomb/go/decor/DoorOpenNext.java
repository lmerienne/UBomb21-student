package fr.ubx.poo.ubomb.go.decor;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;

public class DoorOpenNext extends Decor{
    public DoorOpenNext(Position position){super(position);}

    @Override
    public boolean isWalkable(Player player){
        return true;
     }
}
