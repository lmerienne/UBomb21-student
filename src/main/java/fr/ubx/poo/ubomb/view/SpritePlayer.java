/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.view;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.go.character.Player;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpritePlayer extends Sprite {

    private Effect bloom = new Bloom();
    private final ColorAdjust effect = new ColorAdjust();

    public SpritePlayer(Pane layer, Player player) {
        super(layer, null, player);
        effect.setBrightness(0.8);
        updateImage();
    }

    public SpritePlayer(Pane layer, Player player, Boolean bool) {
        super(layer, null, player);
        effect.setBrightness(0.8);
        ratata(bool);
    }

    public void ratata (Boolean bool){
        Player player = (Player) getGameObject();
        Image image = getImage(player.getDirection());
        if(bool){
            setImage(image, bloom);
        }else{
            setImage(image);
        }
    }

    @Override
    public void updateImage() {
        Player player = (Player) getGameObject();
        Image image = getImage(player.getDirection());
        setImage(image);
    }

    public Image getImage(Direction direction) {
        return ImageResource.getPlayer(direction);
    }

    public void setEffectPlayer(Effect effect){
        Player player = (Player) getGameObject();
        Image image = getImage(player.getDirection());
        setImage(image, bloom);
    }
}
