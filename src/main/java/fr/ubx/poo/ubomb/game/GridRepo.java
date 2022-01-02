package fr.ubx.poo.ubomb.game;

import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.decor.bonus.Bomb_range_inc;
import fr.ubx.poo.ubomb.go.decor.bonus.Key;

import java.io.FileNotFoundException;
import java.io.IOException;


public abstract class GridRepo {

    private final Game game;

    GridRepo(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public abstract Grid load(int level, String name) throws FileNotFoundException, IOException;

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
            case DoorNextClosed:
                return new DoorClose(pos);
            case BombRangeInc:
                return new Bomb_range_inc(pos);
            case Box:
                return new Box(pos);
            case Monster:
                return new Monster(pos);
            case Princess:
                return new Princess(pos);
            case DoorNextOpened:
                return new DoorOpenNext(pos);
            case DoorPrevOpened:
                return new DoorOpenPrev(pos);
            default:
                return null;
                // throw new RuntimeException("EntityCode " + entityCode.name() + " not processed");
        }
    }
}
