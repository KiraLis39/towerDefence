package game.objects;

import java.awt.Graphics2D;
import java.util.List;

public interface iTower {
    AbstractMob scanArea(Graphics2D g2d, List<AbstractMob> currentMobs);

    void attack(AbstractMob aim, Graphics2D g2D);

    void setScanTime(long newScanTime);

    int getCost();

    int getDamage();
}
