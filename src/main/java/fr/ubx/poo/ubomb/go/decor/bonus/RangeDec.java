package fr.ubx.poo.ubomb.go.decor.bonus;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;
import fr.ubx.poo.ubomb.go.decor.Decor;


public class RangeDec extends Bonus {
    public RangeDec(Position position) {
        super(position);
    }

    @Override
    public boolean isWalkable(Player player) {
        return true;
    }

    public void takenBy(Player player) {
        player.takeBonus();
    }
}

