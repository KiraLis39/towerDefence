package game.objects.decorations;

import game.objects.AbstractDecor;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class DefencePlace extends AbstractDecor {

    public DefencePlace(int ID, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("DefenceTowerPlace");

        setId(ID);
        setSpriteList(spriteList);
        setScale(mult);

        setCenterPoint(p);
        setAnimated(false);
        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }

        setComment("Dirt. Just brown-colored dirt under you foots.");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(false);

        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }
    }
}
