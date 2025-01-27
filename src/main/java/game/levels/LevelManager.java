package game.levels;

import com.fasterxml.jackson.databind.ObjectMapper;
import fox.FoxPointConverter;
import game.config.Registry;
import game.core.FoxAudioProcessor;
import game.enums.TowerType;
import game.gui.GameFrame;
import game.mobs.Skeleton;
import game.mobs.Zombie;
import game.objects.AbstractBuilding;
import game.objects.AbstractDecor;
import game.objects.AbstractMob;
import game.objects.buildings.BuildBase;
import game.objects.decorations.DecorBase;
import game.subgui.ItemsFrame;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fox.FoxPointConverter.CONVERT_TYPE.POINT_TO_PERCENT;

@Slf4j
@Getter
public class LevelManager {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<Integer, Level> levels = new LinkedHashMap<>();
    private final Map<AbstractBuilding, Point2D> playerBuilds = new LinkedHashMap<>();
    private final GameFrame gameFrame;
    private int activeLevel = 0;
    private boolean isGameOver = false, isWin = false;
    @Setter
    private ItemsFrame itemsFrame;

    public LevelManager(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    public void reloadMapFolder() {
        if (!Registry.levelsDir.exists()) {
            Registry.levelsDir.mkdir();
        }

        isGameOver = false;
        isWin = false;

        playerBuilds.clear();
        levels.clear();

        try (Stream<Path> stream = Files.list(Path.of(Registry.levelsDir.toString()))) {
            int i = 0;
            for (Path path : stream.collect(Collectors.toSet())) {
                levels.put(i, mapper.readValue(path.toFile(), Level.class));
                i++;
            }

            if (levels.isEmpty()) {
                throw new NoSuchFileException("Levels array is null or empty");
            }

            createDefaultCreativeLevel();
            log.info("Map-files directory is loaded.");
        } catch (IOException e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    private void createDefaultCreativeLevel() throws IOException {
        File defaultLevelMap = Registry.defaultLevelFile;
        if (!defaultLevelMap.exists()) {
            defaultLevelMap.createNewFile();
        }

        levels.put(0, Level.builder().build());
    }

    public void writeNewItemPercentPoint(int itemID, Point2D persentPixelPoint) {
        if (persentPixelPoint == null) {
            System.out.println("LevelManager: writeNewItem(): persentPixelPoint is NULL, than its not been writed in LevelMap.");
            return;
        }

        if (itemID >= 100 && itemID < 200) {
            levels.get(activeLevel).addDecor(DecorBase.get(itemID, persentPixelPoint));
        } else if (itemID >= 200 && itemID < 300) {
            AbstractBuilding playerBuilding = BuildBase.get(itemID, persentPixelPoint);
            levels.get(activeLevel).addBuild(playerBuilding);
            playerBuilds.put(playerBuilding, FoxPointConverter.convert(POINT_TO_PERCENT, persentPixelPoint, gameFrame.getCurrentBounds()));
        }
    }

    public void createNewMob() {
        AbstractMob mob = null;

        if (getZombieCount() > 0) {
            mob = new Zombie(0, Registry.sComb.getSprites("zombie"), getCurrentMobWay());
        } else if (getSkeletonCount() > 0) {
            mob = new Skeleton(1, Registry.sComb.getSprites("skeleton"), getCurrentMobWay());
        }

        if (mob != null) {
            levels.get(activeLevel).createMob(mob);
//			log.info(LevelManager.class, 0, "createNewMob(): creating the " + mob.getName());
        }
    }

    public void removeObject(Point2D point) {
        for (AbstractDecor decor : levels.get(activeLevel).getDeco()) {
            if (decor.getBounds().contains(point)) {
                System.out.println("LevelManager: Removing deco '" + decor.getName() + "' with ID:" + decor.getId() + " from decorationsArray...");
                removeDecor(decor);
                return;
            }
        }

        for (AbstractBuilding build : levels.get(activeLevel).getBuilds()) {
            if (build.getBounds().contains(point)) {
                System.out.println("LevelManager: Removing build '" + build.getName() + "' with ID:" + build.getId() + " from buildsArray...");
                removeBuild(build);
                return;
            }
        }

        for (Point2D p : itemsFrame.getNewGreenKeys()) {
            if (new Rectangle2D.Float((float) p.getX() - 3f, (float) (p.getY() - 3f), 6, 6).contains(point)) {
                System.out.println("LevelManager: Removing MobWayKey '" + point + " from MobWayKeysArray...");
                itemsFrame.removeGreenKey(p);
                return;
            }
        }
    }

    public void clearCurrentLevel() {
        levels.get(activeLevel).clearAll();
    }

    // getters-setters:
    public int getLevelLoadedIndex() {
        return activeLevel;
    }

    public void setLevelLoadedIndex(int levelIndex) {
        activeLevel = levelIndex;
    }

    public int getCurrentLifesCount() {
        return levels.get(activeLevel).getLives();
    }

    public void setCurrentLifesCount(int lifes) {
        levels.get(activeLevel).setLives(lifes);
    }

    public int getCurrentDethsCount() {
        return levels.get(activeLevel).getDeaths();
    }

    public int getZombieCount() {
        return levels.get(activeLevel).getZombiesCount();
    }

    public int getSkeletonCount() {
        return levels.get(activeLevel).getSkeletonsCount();
    }

    public List<AbstractBuilding> getCurrentBuilds() {
        return levels.get(activeLevel).getBuilds();
    }

    public void removeBuild(AbstractBuilding buildEntry) {
        levels.get(activeLevel).removeBuild(buildEntry);
        playerBuilds.remove(buildEntry);
    }

    public void addTower(TowerType type) {
        levels.get(activeLevel).addTower(type);
    }

    public void decreaseTowerCount(TowerType type) {
        levels.get(activeLevel).remTower(type);
    }

    public int getTowersCount(TowerType type) {
        return levels.get(activeLevel).getTowersCount(type);
    }

    public void buildTower(AbstractDecor towerPlace, int towerType) {
        log.info("Build a Tower #" + towerType);

        Point2D p = new Point2D.Double(
                towerPlace.getCenterPoint().getX(),
                towerPlace.getCenterPoint().getY() - towerPlace.getBounds().getHeight() / 2D);

        writeNewItemPercentPoint(towerType == 0 ? 201 : towerType == 1 ? 202 : towerType == 2 ? 203 : 204, p);
    }

    public void removeTower(AbstractBuilding tower) {
        log.info("Destroying the Tower " + tower);
        FoxAudioProcessor.playSound("destroyTower");
        removeBuild(tower);
        gameFrame.addBalls(tower.getCost());
    }

    public List<Point2D> getCurrentMobWay() {
        return levels.get(activeLevel).getMobWay();
    }

    public void correctCurrentMobWay(List<Point2D> newGK) {
        levels.get(activeLevel).setMobWay(newGK);
    }

    public List<Point2D> getCurrentGreenKeysPixels() {
        return levels.get(activeLevel).getCurrentGreenKeysPixels();
    }

    public void decreaseLives() {
        levels.get(activeLevel).setLives(levels.get(activeLevel).getLives() - 1);
        if (levels.get(activeLevel).getLives() <= 0) {
            if (levels.get(activeLevel).getDeaths() <= 0) {
                isGameOver = true;
                FoxAudioProcessor.playSound("sFailSound");
            } else {
                levels.get(activeLevel).setDeaths(levels.get(activeLevel).getDeaths() - 1);
                levels.get(activeLevel).setLives(levels.get(activeLevel).getDefaultLivesCount());
            }
        }
    }

    public List<AbstractMob> getCurrentMobs() {
        return levels.get(activeLevel).getMobs();
    }

    public void removeMob(AbstractMob mob) {
        levels.get(activeLevel).removeMob(mob);
        if (mob instanceof Zombie zombie) {
            levels.get(activeLevel).setZombiesCount(levels.get(activeLevel).getZombiesCount() - 1);
        } else if (mob instanceof Skeleton skeleton) {
            levels.get(activeLevel).setSkeletonsCount(levels.get(activeLevel).getSkeletonsCount() - 1);
        }
    }

    public List<AbstractDecor> getCurrentDeco() {
        return levels.get(activeLevel).getDeco();
    }

    public List<AbstractDecor> getCurrentDeco(int index) {
        List<AbstractDecor> decorByIndex = new LinkedList<>();

        for (AbstractDecor decor : levels.get(activeLevel).getDeco()) {
            if (decor.getId() == index) {
                decorByIndex.add(decor);
            }
        }

        return decorByIndex;
    }

    public void removeDecor(AbstractDecor deco) {
        levels.get(activeLevel).removeDecor(deco);
    }

    public BufferedImage getCurrentBackImage() {
        return levels.get(activeLevel).getBackImage();
    }

    public void setCurrentBackImage(File file) {
        levels.get(activeLevel).setBackImage(file);
    }

    public int getReward(AbstractMob mob) {
        return mob.getName().equals("Zombie") ? 1 : 0;
    }

    public void recheckData() {
        levels.get(activeLevel).getDeco().clear();
    }

    public Level getActiveLevel() {
        return levels.get(activeLevel);
    }
}
