package game.objects.buildings;

import game.objects.AbstractTower;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class RedDefenceTower extends AbstractTower {
    public RedDefenceTower(int id, Point2D p, BufferedImage... spriteList) {
        if (spriteList == null || spriteList[0] == null) {
            throw new RuntimeException("RedDefenceTower: spriteList is NULL");
        }

        setName("RedDefenceTower");

        setId(id);
        setSpriteList(spriteList);
        setScale(0.0006f);
        if (p != null) {
            setCenterPoint(p);
        }

        setComment("Red simple defense tower.");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraws(true);
    }
}
