package game.objects.buildings;

import game.objects.AbstractTower;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class GreenDefenceTower extends AbstractTower {
    public GreenDefenceTower(int id, Point2D p, BufferedImage... spriteList) {
        if (spriteList == null || spriteList[0] == null) {
            throw new RuntimeException("GreenDefenceTower: spriteList is NULL");
        }

        setName("GreenDefenceTower");

        setId(id);
        setSpriteList(spriteList);
        setScale(0.0006f);
        if (p != null) {
            setCenterPoint(p);
        }

        setComment("Green good defense tower.");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraws(true);
    }
}
