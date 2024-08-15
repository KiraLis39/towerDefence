package game.levels;

import fox.FoxPointConverter;
import game.buildings.BuildBase;
import game.config.Registry;
import game.decorations.DecorBase;
import game.enums.TowerType;
import game.objects.AbstractBuilding;
import game.objects.AbstractDecor;
import game.objects.AbstractMob;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import static fox.FoxPointConverter.CONVERT_TYPE.PERCENT_TO_POINT;

@Slf4j
@Getter
@Builder
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
    private String levelBackgroundName = "emptyBackfield";
    @Setter
    @Builder.Default
    private int lives = 3, defaultLivesCount = 3, deaths = 3, zombiesCount = 9, skeletonsCount = 0;

    @Builder.Default
    private int redTowersCount = 1, whiteTowersCount = 1, greenTowersCount = 1, mageTowersCount = 1;


    public Level(String levelName) {
        this.levelName = levelName;
    }

    private String[] getLevelDataString(String index, File mapFile) {
        if (!mapFile.exists()) {
            log.info("Не найдена карта уровня " + index + "! Загрузка уровня прервана. (" + mapFile + ")");
            return null;
        }

        // read the map-file:
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(mapFile), Registry.charset)) {
            int s;
            while ((s = isr.read()) != -1) {
                sb.append((char) s);
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

        // cut map-data by NXT-marker:
        return sb.toString().replaceAll("\n", "").split("NXT");
    }

    private void loadDecorations(String decoData) {
        System.out.println("Load decor...");
        decors.clear();

        Point2D.Double p = new Point2D.Double(
                Double.parseDouble(s.split("=")[1].split(";")[0]),
                Double.parseDouble(s.split("=")[1].split(";")[1])
        );
        Point2D pixPoint = FoxPointConverter.convert(PERCENT_TO_POINT, p, gameFrame.getCurrentBounds());
        decors.add(DecorBase.get(i, pixPoint));
    }

    private void loadBuildings(String bldsData) {
        System.out.println("Load game.buildings...");
        builds.clear();

        String[] dataMassive = bldsData.replace("BUILDS:&", "").split("&"); // pares of the coords by type 'ID=x;y'

        for (int j = 0; j < dataMassive.length; j++) {
            buildHandler(Integer.parseInt(dataMassive[j].substring(0, 3)), new Point2D.Double(
                    Double.parseDouble(dataMassive[j].split("=")[1].split(";")[0]),
                    Double.parseDouble(dataMassive[j].split("=")[1].split(";")[1])));
        }

        for (Entry<AbstractBuilding, Point2D> plbu : levelManager.getPlayerBuilds().entrySet()) {
            Point2D newPoint = FoxPointConverter.convert(PERCENT_TO_POINT, plbu.getValue(), gameFrame.getCurrentBounds());
            AbstractBuilding tow = plbu.getKey();

            tow.setCenterPoint(newPoint);
            builds.add(tow);
        }
    }

    private void buildHandler(int index, Point2D percentFromLevelFile) {
        Point2D pixPoint = FoxPointConverter.convert(PERCENT_TO_POINT, percentFromLevelFile, gameFrame.getCurrentBounds());
        builds.add(BuildBase.get(index, pixPoint));
    }

    private void loadMobwayKeys(String wayData) {
        System.out.println("Load mobway...");
        greenKeysPoints.clear();

        String[] processMassive = wayData.replace("MOBWAY:&", "").split("&");
        System.out.println("Mobway have " + processMassive.length + " points.");

        String[] percentPare;
        Point2D pixelFromPercent;
        for (int j = 0; j < processMassive.length; j++) {
            percentPare = processMassive[j].split(";");
            pixelFromPercent = FoxPointConverter.convert(PERCENT_TO_POINT,
                    new Point2D.Double(Double.parseDouble(percentPare[0]), Double.parseDouble(percentPare[1])), gameFrame.getCurrentBounds());

            greenKeysPoints.add(pixelFromPercent);
        }

        buildAutoWay();
        log.info("loadMobwayKeys(): has build way. MobWay has points totally: " + mobWayPoints.size());
    }


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
            Double xPotense, yPotense;
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
            System.out.println("Removing building '" + bld.getName() + "' with ID:" + bld.getID() + " from level...");
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

    public int getMobsCount() {
        return mobs.size();
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

    public void reCheckData() {
        decors.clear();

        if (levelBackgroundName != null && !builds.isEmpty() && !mobs.isEmpty()) {
            log.info(getClass(), 0, "Level " + levelManager.getLevelLoadedIndex() + " was load correctly.");
        } else {
            log.info(getClass(), 3, "Level " + levelManager.getLevelLoadedIndex() + " is not correctly loaded!");
        }
    }
}
