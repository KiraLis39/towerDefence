package game.objects.buildings;

import game.objects.AbstractBuilding;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class HomeBase extends AbstractBuilding {

    public HomeBase(int id, Point2D p, BufferedImage... spriteList) {
        setName("HomeBase");

        setId(id);
        setSpriteList(spriteList);
        setScale(0.00075f);
        setCenterPoint(p);

        setComment("Defense your Base!");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdraws(true);
    }
}
