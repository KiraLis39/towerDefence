package game.objects;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


public interface iGameObject {
    int getId();

    void setId(int ID);

    String getName();

    void setName(String name);

    String getComment();

    void setComment(String comment);

    ImageIcon getIcon();

    void setIcon(ImageIcon im);

    void setSpriteList(BufferedImage[] newSpriteList);

    float getScale();

    void setScale(float m);

    int getAniSpeed();

    void setAniSpeed(int aniSpeed);

    Point2D getCenterPoint();

    void setCenterPoint(Point2D p);

    Rectangle2D getBounds();

    void draw(Graphics2D g2d, Rectangle2D parentRect);

    boolean isDestroyed();

    void setDestroyed(boolean d);

    boolean isAnimated();

    void setAnimated(boolean d);

    int getCost();

    void setCost(int cost);

    BufferedImage getGhostImage();
}
