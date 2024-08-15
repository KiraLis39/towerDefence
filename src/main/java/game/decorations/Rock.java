package game.decorations;

import game.objects.AbstractDecor;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class Rock extends AbstractDecor {

    public Rock(int id, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("BigRock");

        setID(id);
        setSpritelist(spriteList);
        setScale(mult);
        setCenterPoint(p);
//		setSrcDimension(parentDim);
        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }

        setComment("Very big rock!");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(false);

        createHash(317);
    }
}
