package game.objects;

import java.awt.Graphics2D;

public interface iTower {
    AbstractMob scanArea(Graphics2D g2d);

    void attack(AbstractMob aim, Graphics2D g2D);

    void setScanTime(long newScanTime);

    int getCost();
}
