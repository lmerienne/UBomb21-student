/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.go.decor;


import fr.ubx.poo.ubomb.engine.Timer;
import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Grid;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.view.Sprite;
import fr.ubx.poo.ubomb.view.SpriteFactory;
import fr.ubx.poo.ubomb.view.SpriteMonster;
import javafx.scene.image.Image;
import fr.ubx.poo.ubomb.view.ImageResource;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Monster extends Decor {
    public Direction direction ;
    Timer timer=new Timer(0);

    public Monster(Position position) {
        super(position);
        direction=Direction.UP;
    }
    public final boolean canMove(Direction direction, Game game) {
        Position pos = direction.nextPosition(getPosition());
        Grid grid = game.getGrid();
        Decor element = grid.get(pos);
        if (element instanceof Tree || element instanceof Stone || element instanceof Box || element instanceof DoorClose) {
            return false;
        }
        return game.inside(pos);
    }

    public void update(Game game,long now) throws IOException{
        int velocity=game.monsterVelocity;
        int nbSeconde= 60/velocity;
        long time= 1000000000;
        if(timer.Time(now)>=time*nbSeconde){
           timer.setTime(now);
        if (canMove(direction, game)) {
            doMove(direction, game);
        } else {
            random();
            update(game,now);

        }
        }
    }

    public void random() {
        direction = direction.random();
            }




    public void doMove(Direction direction,Game game) {
        random();;
        Position pos_before=getPosition();
        Position pos_after=direction.nextPosition(getPosition());
        game.getGrid().remove(pos_before);
        Image image = ImageResource.getMonster(this.direction);
        //Sprite.setImage(image);
        this.setPosition(pos_after);

        game.getGrid().set(pos_after,this);
    }
    public Direction getDirection(){return direction;}

}