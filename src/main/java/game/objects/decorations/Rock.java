package game.objects.decorations;

import game.objects.AbstractDecor;
import lombok.Getter;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


@Getter
public class Rock extends AbstractDecor {

    public Rock(int id, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("BigRock");

        setId(id);
        setSpriteList(spriteList);
        setScale(mult);
        setCenterPoint(p);
        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }

        setComment("Very big rock!");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(false);
    }
}
