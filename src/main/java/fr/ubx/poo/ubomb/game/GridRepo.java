package fr.ubx.poo.ubomb.game;

import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.decor.bonus.*;


public abstract class GridRepo {

    private final Game game;

    GridRepo(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public abstract Grid load(int level, String name);

    Decor processEntityCode(EntityCode entityCode, Position pos) {
        switch (entityCode) {
            case Empty:
                return null;
            case Stone:
                return new Stone(pos);
            case Tree:
                return new Tree(pos);
            case Key:
                return new Key(pos);
            case DoorPrevOpened:
                return new Door(pos);
            case BombRangeInc:
                return new RangeInc(pos);
            case Box:
                return new Box(pos);
            case Monster:
                return new Monster(pos);
            case Princess:
                return new Princess(pos);
            case BombNumberInc:
                return new BombInc(pos);
            case BombNumberDec:
                return new BombDec(pos);
            case Heart:
                return new Hearth(pos);
            case BombRangeDec:
                return new RangeDec(pos);
            default:
                return null;
                // throw new RuntimeException("EntityCode " + entityCode.name() + " not processed");
        }
    }
}
