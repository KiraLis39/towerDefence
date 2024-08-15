package game.objects.decorations;

import game.objects.AbstractDecor;
import lombok.Getter;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

@Getter
public class Grave extends AbstractDecor {

    public Grave(int id, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("CrossGrave");

        setId(id);
        setSpriteList(spriteList);
        setScale(mult);
        setCenterPoint(p);
        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }

        setComment("Cross grave with.. somebody inside. Yet..");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(true);

        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }
    }
}
