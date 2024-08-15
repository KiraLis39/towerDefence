package game.objects;

import game.core.FoxAudioProcessor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;


@Setter
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AbstractMob implements iGameObject, iCreature {
    @Builder.Default
    private static final Random rand = new Random();

    private long movingTimeStamp, animateTimeStamp;

    @Setter
    @Getter
    private int id, cost;

    private int xMod, yMod;

    private int imageWidth, imageHeight;

    @Getter
    @Setter
    @Builder.Default
    private int trackStep = 0, slide = 0, walkSpeed = 500, aniSpeed = 250, angle = 0;

    @Setter
    private int hp, damage;

    private String name, comment;

    private List<Point2D> xyList;

    private ImageIcon icon;

    private double memXpoint, memYpoint;

    private float scale;

    @Builder.Default
    private boolean isDestroyed = false, isAnimated = false;

    private Point2D centerPoint;

    @Getter
    private Rectangle2D bounds;

    @Getter
    private BufferedImage[] spriteList;

    @Getter
    private BufferedImage ghostImage;

    private int calculateAngle(Double x, Double y) {
        // angle rotate: // 0 = DOWN; 45 = DOWN_LEFT; 90 = LEFT; 135 = UP_LEFT; 180 = UP;
        if (x > memXpoint - 3 && x < memXpoint + 3) {
            return y <= memYpoint ? 180 : 0;
        } else {
            if (x > memXpoint) {
                if (y > memYpoint) {
                    return -45;
                } else {
                    return y + 3D <= memYpoint ? -135 : -90;
                }
            } else {
                if (y > memYpoint) {
                    return 45;
                } else {
                    return y + 3D <= memYpoint ? 135 : 90;
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g2D, Rectangle2D parentRect) {
        if (xyList.isEmpty()) {
            setDestroyed(true);
            return;
        }

        double x = xyList.get(trackStep).getX();
        double y = xyList.get(trackStep).getY();

        setCenterPoint(new Point2D.Double(x, y));

        if (!isDestroyed()) {
            if (rand.nextInt(150) == 0) {
                FoxAudioProcessor.playSound("sZombieSound_" + rand.nextInt(4));
            }

            angle = calculateAngle(x, y);
            xMod = (int) (bounds.getWidth() / 2);
            yMod = (int) (bounds.getHeight() / 2);

            g2D.translate(getCenterPoint().getX(), getCenterPoint().getY());
            g2D.rotate(Math.toRadians(angle));
            g2D.translate(-xMod, -yMod);

            g2D.drawImage(spriteList[slide],
                    0, 0,
                    (int) bounds.getWidth(),
                    (int) bounds.getHeight(),
                    null);

            g2D.translate(xMod, yMod);
            g2D.rotate(Math.toRadians(-angle));
            g2D.translate(-(getCenterPoint().getX()), -(getCenterPoint().getY()));


            if (isAnimated && System.currentTimeMillis() - animateTimeStamp > aniSpeed) {
                slide = (slide >= spriteList.length) ? 0 : (slide + 1);
                animateTimeStamp = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - movingTimeStamp > walkSpeed) {
                moveNext();
                movingTimeStamp = System.currentTimeMillis();
            }
        }
    }

    public void setWayXY(List<Point2D> xy) {
        this.xyList = xy;
    }

    public void setWayXY(List<Point2D> xy, int wayLenghtWas, int wayLengthNow) {
        this.xyList = xy;
        if (wayLenghtWas > -1 && wayLengthNow > -1) {
            correctStepPosition(wayLenghtWas, wayLengthNow);
        }
    }

    public void moveNext() {
        memXpoint = xyList.get(trackStep).getX();
        memYpoint = xyList.get(trackStep).getY();
        trackStep++;
    }

    public void takeDamage(int _damage) {
        if (hp > 0) {
            hp -= _damage;
            if (hp == 60 || hp == 30) {
                setScale(scale /= 1.5f);
                setWalkSpeed((int) (walkSpeed * 1.5f));
            }
        } else {
            setDestroyed(true);
        }
    }

    @Override
    public void setDestroyed(boolean d) {
        System.out.println("Mob " + getName() + " is destroyed!");
        this.isDestroyed = d;
    }

    private void recheckImageSettings() {
        imageWidth = (int) (spriteList[slide].getWidth() * scale);
        imageHeight = (int) (spriteList[slide].getHeight() * scale);

        xMod = -imageWidth / 2;
        yMod = -imageHeight / 2;
    }

    private void correctStepPosition(int was, int now) {
        trackStep = Math.round((float) now / ((float) was / trackStep));
    }

    @Override
    public void setScale(float m) {
        this.scale = m;
        recheckImageSettings();
    }

    @Override
    public Point2D getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(Point2D p) {
        centerPoint = p;

        // set pixel-rectangle by percents of centerpoint:
        bounds = new Rectangle2D.Double(
                centerPoint.getX() - spriteList[0].getWidth() * scale / 2f,
                centerPoint.getY() - spriteList[0].getHeight() * scale / 2f,
                spriteList[0].getWidth() * scale,
                spriteList[0].getHeight() * scale
        );
    }

    @Override
    public void setSpriteList(BufferedImage[] newSpriteList) {
        this.spriteList = newSpriteList;
        recheckImageSettings();
    }
}
