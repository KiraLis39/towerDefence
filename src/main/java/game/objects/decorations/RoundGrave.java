package game.objects.decorations;

import game.objects.AbstractDecor;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class RoundGrave extends AbstractDecor {

    public RoundGrave(int ID, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("RoundGrave");

        setId(ID);
        setSpriteList(spriteList);
        setScale(mult);
        setCenterPoint(p);

        setComment("Rounded sad grave... rest in peace, mr.Somebody.");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(true);

        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }
    }
}
