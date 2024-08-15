package game.objects.buildings;

import game.objects.AbstractTower;
import lombok.Getter;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

@Getter
public class MageDefenceTower extends AbstractTower {

    public MageDefenceTower(int id, Point2D p, BufferedImage... spriteList) {
        if (spriteList == null || spriteList[0] == null) {
            throw new RuntimeException("MageDefenceTower: spriteList is NULL");
        }

        setName("MageDefenceTower");

        setId(id);
        setSpriteList(spriteList);
        setScale(0.0006f);
        if (p != null) {
            setCenterPoint(p);
        }

        setComment("Mage cool defense tower.");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraws(true);
    }
}
