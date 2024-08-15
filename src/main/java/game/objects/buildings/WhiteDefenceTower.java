package game.objects.buildings;

import game.objects.AbstractTower;
import lombok.Getter;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

@Getter
public class WhiteDefenceTower extends AbstractTower {

    public WhiteDefenceTower(int id, Point2D p, BufferedImage... spriteList) {
        if (spriteList == null || spriteList[0] == null) {
            throw new RuntimeException("WhiteDefenceTower: spriteList is NULL");
        }

        setName("WhiteDefenceTower");

        setId(id);
        setSpriteList(spriteList);
        setScale(0.0006f);
        if (p != null) {
            setCenterPoint(p);
        }

        setComment("White defense tower.");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraws(true);
    }
}
