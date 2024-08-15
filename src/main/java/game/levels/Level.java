package game.levels;

import game.config.Registry;
import game.enums.TowerType;
import game.objects.AbstractBuilding;
import game.objects.AbstractDecor;
import game.objects.AbstractMob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Level {
    @Builder.Default
    private final List<AbstractMob> mobs = new ArrayList<>();

    @Builder.Default
    private final List<AbstractDecor> decors = new ArrayList<>();

    @Builder.Default
    private final List<AbstractBuilding> builds = new ArrayList<>();

    @Builder.Default
    private final List<Point2D> greenKeysPoints = new ArrayList<>();

    @Builder.Default
    private final List<Point2D> greenKeysPercents = new ArrayList<>();

    @Builder.Default
    private final List<Point2D> mobWayPoints = new ArrayList<>();

    private String levelName;

    @Builder.Default
    private String levelBackgroundName = "emptyBackfield";

    @Setter
    @Builder.Default
    private int lives = 3, defaultLivesCount = 3, deaths = 3, zombiesCount = 9, skeletonsCount = 0;

    @Builder.Default
    private int redTowersCount = 1, whiteTowersCount = 1, greenTowersCount = 1, mageTowersCount = 1;

    private long spawnQuantity;

    private void buildAutoWay() {
        List<Point2D> result = new LinkedList<>();
        Point2D lastPoint = null;
        int delimit;

        for (Point2D greenKey : greenKeysPoints) {
            if (lastPoint == null) {
                lastPoint = greenKey;
                result.add(greenKey); // add first point:
                continue;
            }

            if (greenKey.getX() == lastPoint.getX() && greenKey.getY() == lastPoint.getY()) {
                result.add(greenKey);
                continue;
            }

            // add auto-way:
            Point2D wayPoint;
            double xPotense, yPotense;
            delimit = (int) (Math.abs(greenKey.getX() - lastPoint.getX()) + Math.abs(greenKey.getY() - lastPoint.getY())) / 10;

            for (int i = 0; i < delimit; i++) {
                xPotense = (greenKey.getX() - lastPoint.getX()) / delimit * i;
                yPotense = (greenKey.getY() - lastPoint.getY()) / delimit * i;

                wayPoint = new Point2D.Double(
                        lastPoint.getX() + xPotense,
                        lastPoint.getY() + yPotense);

                result.add(wayPoint);
            }

            lastPoint = greenKey;
        }

        mobWayPoints.clear();
        mobWayPoints.addAll(result);
    }

    public void createMob(AbstractMob mob) {
        mobs.add(mob);
    }

    public void removeMob(AbstractMob mob) {
        mobs.remove(mob);
    }

    public void addDecor(AbstractDecor decor) {
        decors.add(decor);
    }

    public void removeDecor(AbstractDecor decor) {
        decors.remove(decor);
    }

    public void addBuild(AbstractBuilding bld) {
        builds.add(bld);
    }

    public void removeBuild(AbstractBuilding bld) {
        if (builds.contains(bld)) {
            System.out.println("Removing building '" + bld.getName() + "' with ID:" + bld.getId() + " from level...");
            builds.remove(bld);
        }
    }

    public void clearAll() {
        greenKeysPercents.clear();
        mobWayPoints.clear();
        mobs.clear();
        decors.clear();
        builds.clear();
    }


    // getters-setters:
    public List<AbstractDecor> getDeco() {
        return decors;
    }

    public BufferedImage getBackImage() {
        return Registry.cache.getBufferedImage(levelBackgroundName);
    }

    public void setBackImage(File image) {
        System.out.println("Swutch background from " + levelBackgroundName + " to " + image.getName());
        levelBackgroundName = image.getName().replace(".png", "");
    }

    public List<Point2D> getCurrentGreenKeysPixels() {
        return greenKeysPoints;
    }

    public List<Point2D> getMobWay() {
        return mobWayPoints;
    }

    public void setMobWay(List<Point2D> newGK) {
        greenKeysPercents.clear();
        greenKeysPercents.addAll(newGK);
        buildAutoWay();
    }

    public int getTowersCount(TowerType type) {
        return switch (type) {
            case WHITE -> whiteTowersCount;
            case GREEN -> greenTowersCount;
            case RED -> redTowersCount;
            default -> mageTowersCount;
        };
    }

    public void addTower(TowerType type) {
        switch (type) {
            case WHITE -> whiteTowersCount++;
            case GREEN -> greenTowersCount++;
            case RED -> redTowersCount++;
            default -> mageTowersCount++;
        }
    }

    public void remTower(TowerType type) {
        switch (type) {
            case RED -> redTowersCount--;
            case WHITE -> whiteTowersCount--;
            case GREEN -> greenTowersCount--;
            default -> mageTowersCount--;
        }
    }

    public String getName() {
        return levelName;
    }
}
