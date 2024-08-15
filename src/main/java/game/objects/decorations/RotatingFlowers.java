package game.objects.decorations;

import game.objects.AbstractDecor;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class RotatingFlowers extends AbstractDecor {

    public RotatingFlowers(int ID, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("RotatingFlowers");

        setId(ID);
        setSpriteList(spriteList);
        setScale(mult);
        setCenterPoint(p);
        setAnimated(true);

        setComment("Little, rotating flowers above your head..");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(true);

        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }
    }
}
