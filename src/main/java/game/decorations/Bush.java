package game.decorations;

import game.objects.AbstractDecor;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class Bush extends AbstractDecor {

    public Bush(int ID, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("LittleBush");

        setID(ID);
        setSpritelist(spriteList);
        setScale(mult);
        setCenterPoint(p);
//		setSrcDimension(parentDim);

        setComment("Some little green bushes.");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(true);

        createHash(48);
    }

}
