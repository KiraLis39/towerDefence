package game.decorations;

import game.objects.AbstractDecor;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class Tree extends AbstractDecor {

    public Tree(int ID, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("Tree");

        setID(ID);
        setSpritelist(spriteList);
        setScale(mult);
        setCenterPoint(p);
//		setSrcDimension(parentDim);

        setComment("Evergreen tree in your field.");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(true);

        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }

        createHash(415);
    }
}
