package game.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

@Slf4j
@Getter
@Setter
public abstract class AbstractBuilding implements iGameObject, iBuild {
    private long animateTimeStamp = System.currentTimeMillis();

    private int ID, slide, animSpeed = 500;

    private String name, comment;

    private float scale;

    private boolean isDestroyed, isOverdraws, isAnimated;

    private ImageIcon icon;

    private Point2D centerPoint;

    private Rectangle2D buildRectangle;

    private BufferedImage[] spriteList;


    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public void setIcon(ImageIcon im) {
        this.icon = im;
    }

    @Override
    public void setSpritelist(BufferedImage[] newSpriteList) {
        this.spriteList = newSpriteList;
    }

    @Override
    public float getScale() {
        return this.scale;
    }

    @Override
    public void setScale(float m) {
        this.scale = m;
    }

    @Override
    public int getAniSpeed() {
        return this.animSpeed;
    }

    @Override
    public void setAniSpeed(int animSpeed) {
        this.animSpeed = animSpeed;
    }

    @Override
    public Point2D getCenterPoint() {
        return centerPoint;
    }

    @Override
    public void setCenterPoint(Point2D p) {
        if (p == null) {
            log.info("Point p is NULL. It`s is temporary ghost object?");
            return;
        }

        centerPoint = p;
        setRectangleByCP();
    }

    @Override
    public Rectangle2D getRectangle() {
        return buildRectangle;
    }

    @Override
    public void draw(Graphics2D g2D, Rectangle2D currentDimension) {
        if (spriteList != null && buildRectangle != null) {
            if (!isDestroyed()) {
                g2D.drawImage(spriteList[slide],
                        (int) buildRectangle.getX(), (int) buildRectangle.getY(),
                        (int) buildRectangle.getWidth(), (int) buildRectangle.getHeight(), null);

                if (isAnimated && System.currentTimeMillis() - animateTimeStamp > animSpeed) {
                    slide++;
                    if (slide >= spriteList.length) {
                        slide = 0;
                    }
                    animateTimeStamp = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public BufferedImage getGhostImage() {
        return spriteList[0];
    }

    public void setRectangleByCP() {
        float scaledWidth = (float) (spriteList[0].getWidth() * (gameFrame.getCurrentBounds().getWidth() * scale));
//		float scaledHeight = (float) (spriteList[0].getHeight() * (gameFrame.getCurrentBounds().getHeight() * scale));

        buildRectangle = new Rectangle2D.Float(
                (float) (centerPoint.getX() - scaledWidth / 2D),
                (float) (centerPoint.getY() - scaledWidth / 2D),
                scaledWidth, scaledWidth
        );
    }

    public Rectangle2D buildSeekArea() {
        return new Rectangle2D.Float(
                (float) (getRectangle().getX() - getSeekDiameter() / 2D),
                (float) (getRectangle().getY() - getSeekDiameter() / 2D),
                (float) (getRectangle().getWidth() + getSeekDiameter()),
                (float) (getRectangle().getHeight() + getSeekDiameter()));
    }

    public abstract double getSeekDiameter();

    public abstract void resetSeekArea();

    public abstract void attack(AbstractMob aim, Graphics2D g2D);

    public abstract int getDamage();
}
