package game.buildings;

import game.gui.StartMenu;
import game.levels.LevelManager;
import game.objects.AbstractBuilding;
import game.objects.AbstractMob;
import game.objects.iTower;
import resources.FoxAudioProcessor;

import javax.swing.ImageIcon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;


public class MageDefenceTower extends AbstractBuilding implements iTower {
    private final int seakDiameter = 400;
    private final int damage = 20;
    private long timeStamp = 0L;
    private long scanTime = 1500L;
    private Rectangle2D seakArea;
    private int cost = 60;

    public MageDefenceTower(int id, Point2D p, BufferedImage... spriteList) {
        if (spriteList == null || spriteList[0] == null) {
            throw new RuntimeException("MageDefenceTower: spriteList is NULL");
        }
//		if (p == null) {throw new RuntimeException("MageDefenceTower: p is NULL");}

        setName("MageDefenceTower");

        setID(id);
        setSpritelist(spriteList);
        setScale(0.0006f);
        if (p != null) {
            setCenterPoint(p);
        }

        setComment("Mage cool defense tower.");
        setIcon(new ImageIcon(spriteList[0]));
        setOverdrawMobs(true);

        createHash(422);
    }

    @Override
    public AbstractMob scanArea(Graphics2D g2D) {
        if (System.currentTimeMillis() - timeStamp > scanTime) {
            timeStamp = System.currentTimeMillis();
//			System.out.println("Tower " + getName() + " (" + getID() + ") scanning area...");

            if (seakArea == null) {
                seakArea = buildSeakArea();
            }
            if (StartMenu.isDebugOn) {
                g2D.setColor(new Color(0.75f, 0.2f, 0.4f, 0.3f));
                g2D.fillRoundRect((int) seakArea.getX(), (int) seakArea.getY(), (int) seakArea.getWidth(), (int) seakArea.getHeight(), 12, 12);
            }

            List<AbstractMob> mobs = LevelManager.getCurrentMobs();
            for (AbstractMob mob : mobs) {
                if (mob.getRectangle().intersects(seakArea)) {
                    return mob;
                }
            }
        }

        return null;
    }

    @Override
    public void setScanTime(long newScanTime) {
        scanTime = newScanTime;
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public void setCost(int _cost) {
        cost = _cost;
    }

    @Override
    public double getSeakDiameter() {
        return seakDiameter;
    }

    @Override
    public void resetSeakArea() {
        seakArea = null;
    }

    @Override
    public void attack(AbstractMob aim, Graphics2D g2D) {
        g2D.setStroke(new BasicStroke(2f));
        g2D.setColor(Color.MAGENTA);
        g2D.drawLine((int) getCenterPoint().getX(), (int) getCenterPoint().getY(), (int) aim.getCenterPoint().getX(), (int) aim.getCenterPoint().getY());
        FoxAudioProcessor.playSound("MageTowerAttack");
        aim.takeDamage(damage);
    }

    @Override
    public int getDamage() {
        return damage;
    }
}
