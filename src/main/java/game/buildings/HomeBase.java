package game.buildings;

import game.objects.AbstractBuilding;
import game.objects.AbstractMob;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class HomeBase extends AbstractBuilding {

    public HomeBase(int id, Point2D p, BufferedImage... spriteList) {
        setName("HomeBase");

        setID(id);
        setSpritelist(spriteList);
        setScale(0.00075f);
        setCenterPoint(p);

        setComment("Defense your Base!");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdrawMobs(true);

        createHash(418);
    }


    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void setCost(int cost) {
    }

    @Override
    public double getSeakDiameter() {
        return 0;
    }

    @Override
    public void resetSeakArea() {
    }

    @Override
    public void attack(AbstractMob aim, Graphics2D g2d) {
    }

    @Override
    public int getDamage() {
        return 0;
    }
}
