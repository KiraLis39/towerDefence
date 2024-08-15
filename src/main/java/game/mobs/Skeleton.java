package game.mobs;

import game.objects.AbstractMob;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;


public class Skeleton extends AbstractMob {

    public Skeleton(int ID, BufferedImage[] spriteList, List<Point2D> wayPoints) {
        setName("Skeleton");
        setID(ID);
        setIcon(new ImageIcon(spriteList[0]));
        setComment("Its mob - Skeleton! Not so slow already.");

        setWayXY(wayPoints);
        setSpritelist(spriteList);
        setScale(0.4f);
        setWalkSpeed(280);
        setHP(120);
        setDamage(2);

        createHash(459);

        movingTimeStamp = System.currentTimeMillis();
        animateTimeStamp = System.currentTimeMillis();
    }
}
