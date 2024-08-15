package game.decorations;

import game.objects.AbstractDecor;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class Grave extends AbstractDecor {

    public Grave(int id, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("CrossGrave");

        setID(id);
        setSpritelist(spriteList);
        setScale(mult);
        setCenterPoint(p);
//		setSrcDimension(parentDim);
        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }

        setComment("Cross grave with.. somebody inside. Yet..");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(true);

        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }

        createHash(5);
    }
}
