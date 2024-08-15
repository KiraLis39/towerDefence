package game.decorations;

import game.objects.AbstractDecor;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class WavingGrass extends AbstractDecor {

    public WavingGrass(int id, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("WavingGrass");

        setID(id);
        setSpritelist(spriteList);
        setScale(mult);
        setCenterPoint(p);
        setAnimated(true);

        setComment("Animated waving grass... beauty, not it?");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(true);

        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }

        createHash(417);
    }
}
