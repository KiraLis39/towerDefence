package game.objects.decorations;

import game.objects.AbstractDecor;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class Mushroom extends AbstractDecor {

    public Mushroom(int id, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("MushrumBrown");

        setId(id);
        setSpriteList(spriteList);
        setScale(mult);
        if (p != null) {
            setCenterPoint(p);
        }

        setComment("Mushrum. Not eat!");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(true);

        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }
    }
}
