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

    @Getter
    @Setter
    private int id, slide, aniSpeed = 500, damage;

    @Getter
    @Setter
    private int cost = 20;

    private String name, comment;

    private float scale;

    @Setter
    private boolean isDestroyed, isOverdraws, isAnimated;

    private ImageIcon icon;

    @Getter
    private Point2D centerPoint;

    @Getter
    private Rectangle2D bounds;

    private BufferedImage[] spriteList;

    @Override
    public void setCenterPoint(Point2D centerPoint) {
        this.centerPoint = centerPoint;
    }

    @Override
    public void draw(Graphics2D g2D, Rectangle2D currentDimension) {
        if (spriteList != null && bounds != null) {
            if (!isDestroyed()) {
                g2D.drawImage(spriteList[slide],
                        (int) bounds.getX(), (int) bounds.getY(),
                        (int) bounds.getWidth(), (int) bounds.getHeight(), null);

                if (isAnimated && System.currentTimeMillis() - animateTimeStamp > aniSpeed) {
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

    public void calculateRectangleByCenterPoint() {
        float scaledWidth = (float) (spriteList[0].getWidth() * (getBounds().getWidth() * scale));
//		float scaledHeight = (float) (spriteList[0].getHeight() * (bounds.getHeight() * scale));

        bounds = new Rectangle2D.Float(
                (float) (centerPoint.getX() - scaledWidth / 2D),
                (float) (centerPoint.getY() - scaledWidth / 2D),
                scaledWidth, scaledWidth
        );
    }
}
