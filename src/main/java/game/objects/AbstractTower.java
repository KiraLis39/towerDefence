package game.objects;

import game.core.FoxAudioProcessor;
import lombok.Getter;
import lombok.Setter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

@Getter
@Setter
public class AbstractTower extends AbstractBuilding implements iTower {
    private Rectangle2D seekArea;
    private int seekDiameter = 300;
    private int damage = 15;
    private long scanTime = 1200L, was;

    public Rectangle2D buildSeekArea() {
        return new Rectangle2D.Float(
                (float) (getBounds().getX() - getSeekDiameter() / 2D),
                (float) (getBounds().getY() - getSeekDiameter() / 2D),
                (float) (getBounds().getWidth() + getSeekDiameter()),
                (float) (getBounds().getHeight() + getSeekDiameter()));
    }

    public void resetSeekArea() {
        seekArea = null;
    }

    @Override
    public AbstractMob scanArea(Graphics2D g2D, List<AbstractMob> currentMobs) {
        if (System.currentTimeMillis() - was > scanTime) {
            was = System.currentTimeMillis();
//			System.out.println("Tower " + getName() + " (" + getID() + ") scanning area...");

            if (seekArea == null) {
                seekArea = buildSeekArea();
            }

            g2D.setColor(new Color(0.75f, 0.2f, 0.4f, 0.3f));
            g2D.fillRoundRect((int) seekArea.getX(), (int) seekArea.getY(), (int) seekArea.getWidth(), (int) seekArea.getHeight(), 12, 12);

            return getFirstMobIntoArea(currentMobs, seekArea);
        }

        return null;
    }

    public AbstractMob getFirstMobIntoArea(List<AbstractMob> currentMobs, Rectangle2D seekArea) {
        for (AbstractMob mob : currentMobs) {
            if (mob.getBounds().intersects(seekArea)) {
                return mob;
            }
        }
        return null;
    }

    @Override
    public void attack(AbstractMob aim, Graphics2D g2D) {
        g2D.setStroke(new BasicStroke(2f));
        g2D.setColor(Color.GREEN);
        g2D.drawLine((int) getCenterPoint().getX(), (int) getCenterPoint().getY(), (int) aim.getCenterPoint().getX(), (int) aim.getCenterPoint().getY());
        FoxAudioProcessor.playSound("GreenTowerAttack");
        aim.takeDamage(damage);
    }

    @Override
    public void setScanTime(long newScanTime) {
        scanTime = newScanTime;
    }
}
