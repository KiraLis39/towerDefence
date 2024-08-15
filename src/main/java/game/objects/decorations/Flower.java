package game.objects.decorations;

import game.objects.AbstractDecor;
import lombok.Getter;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

@Getter
public class Flower extends AbstractDecor {

    public Flower(int ID, BufferedImage[] spriteList, Point2D p, float mult, int animSpeed) {
        setName("FlowerMagenta");

        setId(ID);
        setSpriteList(spriteList);
        setScale(mult);
        setCenterPoint(p);

        setComment("Its flower. Whats up!");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(false);

        if (animSpeed != -1) {
            setAniSpeed(animSpeed);
        }
    }
}
