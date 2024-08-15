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
public abstract class AbstractDecor implements iGameObject, iDecoration {
    private long animateTimeStamp = System.currentTimeMillis();

    private int id, slide, animSpeed = -1, cost;

    private String name, comment;

    private long hash;

    private float scale;

    private boolean isDestroyed = false, isOverdraws = false, isAnimated = false;

    private ImageIcon icon;

    private Point2D centerPoint;

    private Rectangle2D bounds;

    private BufferedImage[] spriteList;

    @Override
    public Point2D getCenterPoint() {
        return centerPoint;
    }

    @Override
    public void setCenterPoint(Point2D p) {
        if (p == null) {
            log.info("PercentPoint p is NULL. It`s just temporary ghost object?");
            return;
        }
        centerPoint = p;
        setRectangleByCP();
    }

    @Override
    public void draw(Graphics2D g2D, Rectangle2D currentDimension) {
        if (spriteList != null) {
            if (!isDestroyed()) {
                g2D.drawImage(spriteList[slide],
                        (int) bounds.getX(), (int) bounds.getY(),
                        (int) bounds.getWidth(), (int) bounds.getHeight(), null);

                if (animSpeed != -1 && isAnimated && System.currentTimeMillis() - animateTimeStamp > animSpeed) {
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
        float scaledWidth = (float) (spriteList[0].getWidth() * (bounds.getWidth() * scale));
//		float scaledHeight = (float) (spriteList[0].getHeight() * (bounds.getHeight() * scale));

        bounds = new Rectangle2D.Float(
                (float) (centerPoint.getX() - scaledWidth / 2D),
                (float) (centerPoint.getY() - scaledWidth / 2D),
                scaledWidth, scaledWidth
        );

        // set pixel-rectangle by percents of centerpoint:
//		bounds = new Rectangle2D.Float(
//				(float) (centerPoint.getX() - spriteList[0].getWidth() * scale / 2f),
//				(float) (centerPoint.getY() - spriteList[0].getHeight() * scale / 2f),
//				(float) spriteList[0].getWidth() * scale, (float) spriteList[0].getHeight() * scale);
    }

    @Override
    public boolean isOverdraw() {
        return this.isOverdraws;
    }

    @Override
    public void setOverdraw(boolean isOverdraw) {
        this.isOverdraws = isOverdraw;
    }

    @Override
    public int getAniSpeed() {
        return this.animSpeed;
    }

    @Override
    public void setAniSpeed(int aniSpeed) {
        this.animSpeed = aniSpeed;
    }
}
