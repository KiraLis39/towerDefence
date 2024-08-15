package game.mobs;

import game.objects.AbstractMob;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;


public class Skeleton extends AbstractMob {

    public Skeleton(int ID, BufferedImage[] spriteList, List<Point2D> wayPoints) {
        setName("Skeleton");
        setId(ID);
        setIcon(new ImageIcon(spriteList[0]));
        setComment("Its mob - Skeleton! Not so slow already.");

        setWayXY(wayPoints);
        setSpriteList(spriteList);
        setScale(0.4f);
        setWalkSpeed(280);
        setHp(120);
        setDamage(2);

        setMovingTimeStamp(System.currentTimeMillis());
        setAnimateTimeStamp(System.currentTimeMillis());
    }
}
