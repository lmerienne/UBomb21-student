/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.view;

import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.decor.bonus.Bomb_range_inc;
import fr.ubx.poo.ubomb.go.decor.bonus.Key;
import javafx.scene.layout.Pane;

import static fr.ubx.poo.ubomb.view.ImageResource.*;


public final class SpriteFactory {

    public static Sprite create(Pane layer, GameObject gameObject) {
        if (gameObject instanceof Stone)
            return new Sprite(layer, STONE.getImage(), gameObject);
        if (gameObject instanceof Tree)
            return new Sprite(layer, TREE.getImage(), gameObject);
        if (gameObject instanceof Key)
            return new Sprite(layer, KEY.getImage(), gameObject);
        if (gameObject instanceof Door)
            return new Sprite(layer, DOOR_CLOSED.getImage(), gameObject);
        if (gameObject instanceof Bomb_range_inc)
            return new Sprite(layer, BONUS_BOMB_RANGE_INC.getImage(), gameObject);
        if (gameObject instanceof Box)
            return new Sprite(layer, BOX.getImage(), gameObject);
        if (gameObject instanceof Monster)
            return new Sprite(layer, MONSTER_UP.getImage(), gameObject);
        if (gameObject instanceof Princess)
            return new Sprite(layer, PRINCESS.getImage(), gameObject);
        throw new RuntimeException("Unsupported sprite for decor " + gameObject);
    }
}
