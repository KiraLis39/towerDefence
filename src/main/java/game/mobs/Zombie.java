package game.mobs;

import game.objects.AbstractMob;
import lombok.Getter;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;


@Getter
public class Zombie extends AbstractMob {

    private final Zombie zombie;

    public Zombie(int id, BufferedImage[] spriteList, List<Point2D> wayPoints) {
        super();
        this.zombie = build(id, spriteList, wayPoints);
    }

    public Zombie build(int id, BufferedImage[] spriteList, List<Point2D> wayPoints) {
        AbstractMob zombie = AbstractMob.builder()
                .id(id)
                .name("Zombie")
                .icon(new ImageIcon(spriteList[0]))
                .comment("Its mob - Zombie. Slow and weak.")
                .xyList(wayPoints)
                .spriteList(spriteList)
                .scale(0.3f)
                .walkSpeed(300)
                .hp(100)
                .damage(1)
                .build();

        super.setMovingTimeStamp(System.currentTimeMillis());
        super.setAnimateTimeStamp(System.currentTimeMillis());

        return (Zombie) zombie;
    }
}
