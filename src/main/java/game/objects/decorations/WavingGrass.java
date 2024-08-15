package game.objects.decorations;

import game.objects.AbstractDecor;
import lombok.Getter;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

@Getter
public class WavingGrass extends AbstractDecor {

    public WavingGrass(int id, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("WavingGrass");

        setId(id);
        setSpriteList(spriteList);
        setScale(mult);
        setCenterPoint(p);
        setAnimated(true);

        setComment("Animated waving grass... beauty, not it?");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(true);

        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }
    }
}
