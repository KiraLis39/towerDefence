package game.decorations;

import game.objects.AbstractDecor;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class Flower extends AbstractDecor {

    public Flower(int ID, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("FlowerMagenta");

        setID(ID);
        setSpritelist(spriteList);
        setScale(mult);
        setCenterPoint(p);
//		setSrcDimension(parentDim);

        setComment("Its flower. Whats up!");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(false);

        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }

        createHash(341);
    }
}
