package game.objects.decorations;

import game.objects.AbstractDecor;
import lombok.Getter;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

@Getter
public class Bush extends AbstractDecor {

    public Bush(int ID, BufferedImage[] spriteList, Point2D p, float mult, int aniSpeed) {
        setName("LittleBush");

        setId(ID);
        setSpriteList(spriteList);
        setScale(mult);
        setCenterPoint(p);
        setAniSpeed(aniSpeed);

        setComment("Some little green bushes.");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraw(true);
    }
}
