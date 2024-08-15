package game.decorations;

import game.objects.AbstractDecor;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class Mushrum extends AbstractDecor {

    public Mushrum(int id, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("MushrumBrown");

        setID(id);
        setSpritelist(spriteList);
        setScale(mult);
        if (p != null) {
            setCenterPoint(p);
        }
//		setSrcDimension(parentDim);

        setComment("Mushrum. Not eat!");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(true);

        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }

        createHash(17);
    }
}
