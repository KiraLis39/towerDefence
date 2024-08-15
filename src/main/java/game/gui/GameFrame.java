package game.gui;

import fox.FoxFontBuilder;
import fox.images.FoxCursor;
import game.buildings.BuildBase;
import game.config.FoxAudioProcessor;
import game.config.Registry;
import game.config.SavesEngine;
import game.decorations.DecorBase;
import game.levels.LevelManager;
import game.objects.AbstractBuilding;
import game.objects.AbstractDecor;
import game.objects.AbstractMob;
import game.objects.iGameObject;
import game.objects.iTower;
import game.subgui.ItemsFrame;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class GameFrame extends JFrame implements WindowStateListener, ComponentListener, MouseListener, MouseMotionListener, WindowListener, Runnable {
    private static final int FRAME_WIDTH = 1440, FRAME_HEIGHT = 900;
    private static BufferedImage memoryImage, ghostImage;

    @Getter
    private static MouseMotionListener MMList;

    @Getter
    private static MouseListener MList;
    private static JFrame gameFrame;
    private static ItemsFrame itemsFrame;
    private static Boolean isPaused = false, isCreativeMode;
    private static int balls = 0, memoryDecorID;
    private final Canvas canva;
    private final Rectangle2D[] upInfoRect = new Rectangle2D[6];
    private final Rectangle2D[] downCardRect = new Rectangle2D[4];
    private final int fullscreenKey = KeyEvent.VK_F11;
    private final LevelManager levelManager = new LevelManager();
    private BufferStrategy bs;
    private BufferedImage[] infos, cards;
    private Point mousePoint;
    private Font upCardsFont, downCardsFont, errTipFont, debugFont, littleTechFont;
    private Boolean isStoryPlayed = false, itemsInFocus = false;
    private Boolean redTowerOver = false, whiteTowerOver = false, greenTowerOver = false, mageTowerOver = false;
    private Boolean redTowerIsChosen = false, whiteTowerIsChosen = false, greenTowerIsChosen = false, mageTowerIsChosen = false;
    private long spawnTimer;
    private List<Point2D> mobWay;
    private List<AbstractDecor> towerPlaces;


    public GameFrame() {
        this(-1);
    }

    public GameFrame(int modeCode) {
        isCreativeMode = StartMenu.cmodeUnlocked && modeCode == 139;
        initialization();

        gameFrame = this;
        MMList = this;
        MList = this;

        setTitle("Tower defence! v." + Registry.verse);
        setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        canva = new Canvas(getGraphicsConfiguration());
        canva.addMouseListener(GameFrame.this);
        canva.addMouseMotionListener(GameFrame.this);

        add(canva);

        addComponentListener(this);
        addWindowListener(this);
        addWindowStateListener(this);

        setLocationRelativeTo(null);
        setVisible(true);

        canva.createBufferStrategy(2);

        loadGame(levelManager.getLevelLoadedIndex());

        new Thread(this).start();
    }

    public static void setsCursor(Cursor cur) {
        gameFrame.setCursor(cur);
    }

    private static void setPause(boolean p) {
        isPaused = p;
    }

    public static void resetMemoryImage() {
        ghostImage = null;
        memoryImage = null;
    }

    public static void resetMemoryDecorID() {
        memoryDecorID = -1;
    }

    public static void addBalls(int cost) {
        balls += cost;
    }

    public static JFrame getOwnFrame() {
        return gameFrame;
    }

    public Rectangle2D getCurrentBounds() {
        return this.canva.getBounds();
    }

    private void initialization() {
        setupInAc();

        errTipFont = Registry.ffb.setFoxFont(FoxFontBuilder.FONT.HARLOW_S_I, 48, false);
        downCardsFont = Registry.ffb.setFoxFont(FoxFontBuilder.FONT.HARLOW_S_I, 30, false);
        try {
            upCardsFont = Font.createFont(Font.TRUETYPE_FONT, new File("./resources/fonts/papyrus.ttf"));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
        debugFont = Registry.ffb.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 24, false);
        littleTechFont = Registry.ffb.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 16, false);

        Registry.sComb.getSprites("decos", getImage("decoList"), 4, 3);
        Registry.sComb.getSprites("decos_2", getImage("decoList_2"), 4, 4);
        Registry.sComb.getSprites("decos_3", getImage("decoList_3"), 4, 3);
        Registry.sComb.getSprites("clouds", getImage("cloudsList"), 3, 2);
        Registry.sComb.getSprites("grass", getImage("grassList"), 3, 2);
        Registry.sComb.getSprites("defPlace", getImage("defPoint"), 1, 1);

        if (isCreativeMode()) {
            return;
        }

        infos = Registry.sComb.getSprites("infos", getImage("infoList"), 3, 3);
        cards = Registry.sComb.getSprites("cards", getImage("cardList"), 4, 2);

        Registry.sComb.getSprites("zombie", getImage("mobZombieList"), 3, 2);
    }

    private BufferedImage getImage(String name) {
        return Registry.cache.getBufferedImage(name);
    }

    private void setupInAc() {
        Registry.inAc.add("levelFrame", getRootPane());
        Registry.inAc.set(JComponent.WHEN_IN_FOCUSED_WINDOW, "levelFrame", "swichFullscreen", fullscreenKey, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Registry.userConf.setFullScreen(!Registry.userConf.isFullScreen());
                setFullScreen(Registry.userConf.isFullScreen());
            }
        });
    }

    private void loadGame(int index) {
        levelManager.reloadMapFolder();

        if (isCreativeMode && (itemsFrame == null || !itemsFrame.isVisible())) {
            log.info("Creating a new CreatorItemsFrame()...");
            levelManager.setLevelLoadedIndex(0);
            itemsFrame = new ItemsFrame();
        } else {
            if (levelManager.getLevelLoadedIndex() != index) {
                log.info("Loading the level " + index + "...");
                levelManager.setLevelLoadedIndex(index);
                rebuildRectangles();
            }
        }

        setPause(false);
    }

    // game mechanic:
    private void spawnMob() {
        if (isCreativeMode()) {
            return;
        }
        levelManager.createNewMob();
    }

    // creating map:
    private void changeItemBrush(MouseEvent e) {
        if (e.getSource() instanceof JButton && itemsInFocus) {
            if (((JButton) e.getSource()).getName().startsWith("itemButton_")) {
                Icon ico = ((JButton) e.getSource()).getIcon();
                BufferedImage tmp = new BufferedImage(ico.getIconWidth(), ico.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = (Graphics2D) tmp.getGraphics();

                g2d.setStroke(new BasicStroke(1));
//				g2d.setColor(Color.WHITE);
//				g2d.fillOval(0, 0, tmp.getWidth(), tmp.getHeight());
                g2d.setColor(Color.YELLOW);
                g2d.fillRect(0, 0, 16, 16);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawOval(0, 0, tmp.getWidth(), tmp.getHeight());
                ico.paintIcon(null, g2d, 0, 0);

                g2d.dispose();
                memoryImage = tmp;
                memoryDecorID = Integer.parseInt(((JButton) e.getSource()).getName().split("_")[1]);
                setsCursor(FoxCursor.createCursor(memoryImage, ((JButton) e.getSource()).getName()));
                ItemsFrame.freeMouse = false;


                iGameObject gob = null;
                try {
                    gob = DecorBase.get(memoryDecorID, null);
                    assert gob != null;
                    tmp = gob.getGhostImage();
                } catch (Exception e2) {
                    gob = BuildBase.get(memoryDecorID, null);
                    tmp = gob.getGhostImage();
                } finally {
                    ghostImage = new BufferedImage(
                            (int) (tmp.getWidth() * (getCurrentBounds().getWidth() * (gob != null ? gob.getScale() : 1))),
                            (int) (tmp.getHeight() * (getCurrentBounds().getWidth() * (gob != null ? gob.getScale() : 1))), BufferedImage.TYPE_INT_ARGB);
                    g2d = (Graphics2D) ghostImage.getGraphics();
                    g2d.drawImage(tmp, 0, 0, ghostImage.getWidth(), ghostImage.getHeight(), null);
                    g2d.dispose();
                }
            }
        }
    }

    private void selectObject(MouseEvent e) {
        Component c = (Component) e.getSource();
        if (c instanceof JButton) {
            if (c.getName().startsWith("itemButton_")) {
                c.setBackground(Color.GREEN.brighter());
            }
        }
    }

    private void deselectObject(MouseEvent e) {
        Component c = (Component) e.getSource();
        if (c instanceof JButton) {
            if (c.getName().startsWith("itemButton_")) {
                c.setBackground(Color.GRAY);
            }
        }
    }

    // DRAW RUNNABLE:
    @Override
    public void run() {
        isStoryPlayed = true;
        spawnTimer = System.currentTimeMillis();

        while (isStoryPlayed) {
            if (isPaused) {
                Thread.yield();
                continue;
            }

            if (canva == null || canva.getBufferStrategy() == null) {
                canva.createBufferStrategy(2);
            }

            try {
                bs = canva.getBufferStrategy();
                do {
                    do {
                        update((Graphics2D) bs.getDrawGraphics());
                    } while (bs.contentsRestored());
                } while (bs.contentsLost());
                bs.show();
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
            }

            if (System.currentTimeMillis() - spawnTimer > levelManager.spawnQuantity && !levelManager.isWin() && !levelManager.isGameover()) {
                spawnMob();
                spawnTimer = System.currentTimeMillis();
            }
            try {
                Thread.sleep(32);
            } catch (InterruptedException e) {
                log.error("Error: {}", e.getMessage());
            }
        }
    }

    private void update(Graphics2D g2D) {
        if (getWidth() <= 100 || getHeight() <= 50) {
            return;
        }

        rendering(g2D); // rendering hooks

        drawBackfield(g2D); // background
        drawGroundDeco(g2D); // ground details
        drawBuildings(g2D); // game.buildings
        drawUpperDeco(g2D); // game.decorations
        if (StartMenu.isDebugOn) {
            drawMobway(g2D);
        } // draw mobway line

        if (ghostImage != null) {
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
            g2D.drawImage(ghostImage,
                    mousePoint.x - ghostImage.getWidth() / 2,
                    mousePoint.y - ghostImage.getHeight() / 2,
                    ghostImage.getWidth(),
                    ghostImage.getHeight(),
                    canva);
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        g2D.setFont(debugFont);
        g2D.drawString("Fullscreen: " + KeyEvent.getKeyText(fullscreenKey), 25, 55);

        if (!isCreativeMode()) {
            if (levelManager.isGameover()) {
                drawUpCards(g2D); // upper info cards
                drawDownCards(g2D); // down towers cards
                g2D.setFont(upCardsFont);
                g2D.drawString("GAMEOVER",
                        (int) (getCurrentBounds().getWidth() / 2 - Registry.ffb.getStringBounds(g2D, "GAMEOVER").getWidth() / 2),
                        (int) (getCurrentBounds().getHeight() / 2 + debugFont.getSize()));

                g2D.dispose();
                return;
            }

            drawMobs(g2D); // game.mobs
            checkTowerActivity(g2D); // towers fire!
            drawUpCards(g2D); // upper info cards
            drawDownCards(g2D); // down towers cards
        }

        g2D.dispose();
    }

    private void rendering(Graphics2D g2D) {
//			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        if (StartMenu.useSmoothing) {
            if (StartMenu.useBicubic) {
                g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            } else {
                g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }
        } else {
            g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        }
//			g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//			g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//			g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private void drawBackfield(Graphics2D g2D) {
        g2D.setColor(Color.BLACK);
        g2D.fillRect(0, 0, canva.getWidth(), canva.getHeight());

        g2D.setFont(errTipFont);
        if (levelManager.getCurrentBackImage() == null) {
            g2D.setColor(Color.RED);
            g2D.drawString("NO MAP LOADED",
                    (int) (canva.getWidth() / 2 - Registry.ffb.getStringBounds(g2D, "NO MAP LOADED").getWidth() / 2D),
                    (int) (canva.getHeight() / 2 - Registry.ffb.getStringBounds(g2D, "NO MAP LOADED").getHeight() / 5D));
        } else {
            g2D.setColor(Color.GREEN);
            g2D.drawImage(levelManager.getCurrentBackImage(), 0, 0, canva.getWidth(), canva.getHeight(), canva);
        }
    }

    private void drawGroundDeco(Graphics2D g2D) {
        // draw by PERCENT coordinates:
        for (AbstractDecor decoEntry : levelManager.getCurrentDeco()) {
            if (decoEntry.isOverdraw()) {
                continue;
            }

            if (decoEntry.isDestroyed()) {
                System.out.println("Object '" + decoEntry.getName() + "' marked as 'Destroyed' than remove it from deco-map...");
                levelManager.removeDecor(decoEntry);
            } else {
                decoEntry.draw(g2D, gameFrame.getBounds());

                if (StartMenu.isDebugOn) {
                    g2D.setColor(Color.ORANGE);
                    g2D.drawRoundRect(
                            (int) decoEntry.getRectangle().getX(),
                            (int) decoEntry.getRectangle().getY(),
                            (int) decoEntry.getRectangle().getWidth(),
                            (int) decoEntry.getRectangle().getHeight(), 9, 9);
                }
            }
        }
    }

    private void drawBuildings(Graphics2D g2D) {
        // draw by PERCENT coordinates:
        for (AbstractBuilding buildEntry : levelManager.getCurrentBuilds()) {
            try {
                if (buildEntry.isDestroyed()) {
                    System.out.println("Object '" + buildEntry.getName() + "' marked as 'Destroyed' than remove it from build-map...");
                    levelManager.removeBuild(buildEntry);
                } else {
                    buildEntry.draw(g2D, gameFrame.getBounds());

                    if (StartMenu.isDebugOn) {
                        try {
                            g2D.setColor(Color.CYAN);
                            g2D.drawRoundRect(
                                    (int) buildEntry.getRectangle().getX(),
                                    (int) buildEntry.getRectangle().getY(),
                                    (int) buildEntry.getRectangle().getWidth(),
                                    (int) buildEntry.getRectangle().getHeight(), 9, 9);
                        } catch (Exception e) {
                            log.error("Не могу отрисовать ректангл объекта " + buildEntry.getName()
                                    + " #" + buildEntry.getID() + ": " + e.getMessage());
                        }
                    }

                    if (redTowerIsChosen || whiteTowerIsChosen || greenTowerIsChosen || mageTowerIsChosen) {
                        if (towerPlaces == null) {
                            towerPlaces = levelManager.getCurrentDeco(199);
                        }

                        for (AbstractDecor tPlace : towerPlaces) {
                            if (tPlace.getRectangle().contains(mousePoint)) {
                                g2D.setColor(Color.GREEN);
                                g2D.drawRoundRect(
                                        (int) tPlace.getRectangle().getX(),
                                        (int) tPlace.getRectangle().getY(),
                                        (int) tPlace.getRectangle().getWidth(),
                                        (int) tPlace.getRectangle().getHeight(), 9, 9);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
            }
        }
    }

    private void drawUpperDeco(Graphics2D g2D) {
        // draw by PERCENT coordinates:
        for (AbstractDecor deco : levelManager.getCurrentDeco()) {
            if (!deco.isOverdraw()) {
                continue;
            }

            if (deco.isDestroyed()) {
                System.out.println("Object '" + deco.getName() + "' marked as 'Destroyed' than remove it from deco-map...");
                levelManager.removeDecor(deco);
            } else {
                deco.draw(g2D, gameFrame.getBounds());

                if (StartMenu.isDebugOn) {
                    g2D.setColor(Color.ORANGE);
                    g2D.drawRoundRect(
                            (int) deco.getRectangle().getX(),
                            (int) deco.getRectangle().getY(),
                            (int) deco.getRectangle().getWidth(),
                            (int) deco.getRectangle().getHeight(), 9, 9);
                }
            }
        }
    }

    private void drawMobway(Graphics2D g2D) {
        g2D.setColor(Color.RED);
        g2D.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{10, 10, 10, 10}, 1));

        if (mobWay.size() == 1) {
            g2D.drawOval(
                    (int) ((mobWay.get(0).getX() * 100f - 3f)),
                    (int) ((mobWay.get(0).getY() * 100f - 3f)),
                    6, 6);
            return;
        }

        for (int i = 1; i < mobWay.size(); i++) {
            g2D.drawLine(
                    (int) (mobWay.get(i - 1).getX()),
                    (int) (mobWay.get(i - 1).getY()),
                    (int) (mobWay.get(i).getX()),
                    (int) (mobWay.get(i).getY()));
        }

        g2D.setFont(littleTechFont);
        g2D.setColor(Color.GREEN);
        int pointCounter = 1;
        for (Point2D mw : levelManager.getCurrentGreenKeysPixels()) {
            g2D.fillOval(
                    (int) ((mw.getX() - 3f)),
                    (int) ((mw.getY() - 3f)),
                    6, 6);

            g2D.drawString(String.valueOf(pointCounter),
                    (float) (mw.getX() - 4f),
                    (float) (mw.getY() - 4f));

            pointCounter++;
        }
    }

    private void drawMobs(Graphics2D g2D) {
        // draw by PERCENT coordinates:
        for (AbstractMob mob : levelManager.getCurrentMobs()) {
            if (mob.isDestroyed()) {
                System.out.println("Object '" + mob.getName() + "' marked as 'Destroyed' than remove it from mob-array...");
                balls += levelManager.getReward(mob);
                FoxAudioProcessor.playSound("sZombieSound_5");
                levelManager.removeMob(mob);
            } else {
                mob.draw(g2D, gameFrame.getBounds());
                if (mob.getTrackStep() >= mob.getXyList().size()) {
                    mob.setDestroyed(true);
                    levelManager.decreaseLives();
                    System.out.println("AbstractMob: Mob " + getName() + " has been destroyed by pass-end.");
                }

                if (StartMenu.isDebugOn) {
                    g2D.setColor(Color.YELLOW);

                    Rectangle2D rect = mob.getRectangle();
                    g2D.drawRect((int) rect.getX(), (int) (rect.getY()), (int) rect.getWidth(), (int) rect.getHeight());
                }
            }
        }
    }

    private void checkTowerActivity(Graphics2D g2D) {
        AbstractMob aim;
        for (AbstractBuilding building : levelManager.getCurrentBuilds()) {
            if (building instanceof iTower) {
                if ((aim = ((iTower) building).scanArea(g2D)) != null) {
                    ((iTower) building).attack(aim, g2D);
                }
            }
        }
    }

    private void drawUpCards(Graphics2D g2D) {
        float textWidth, textHeight;
        int upCmod;

        g2D.drawImage(infos[0], (int) upInfoRect[0].getX(), (int) upInfoRect[0].getY(), (int) upInfoRect[0].getWidth(), (int) upInfoRect[0].getHeight(), canva);
        g2D.drawImage(infos[6], (int) upInfoRect[1].getX(), (int) upInfoRect[1].getY(), (int) upInfoRect[1].getWidth(), (int) upInfoRect[1].getHeight(), canva);

        g2D.drawImage(infos[2], (int) upInfoRect[2].getX(), (int) upInfoRect[2].getY(), (int) upInfoRect[2].getWidth(), (int) upInfoRect[2].getHeight(), canva);
        g2D.drawImage(infos[6], (int) upInfoRect[3].getX(), (int) upInfoRect[3].getY(), (int) upInfoRect[3].getWidth(), (int) upInfoRect[3].getHeight(), canva);

        g2D.drawImage(infos[3], (int) upInfoRect[4].getX(), (int) upInfoRect[4].getY(), (int) upInfoRect[4].getWidth(), (int) upInfoRect[4].getHeight(), canva);
        g2D.drawImage(infos[6], (int) upInfoRect[5].getX(), (int) upInfoRect[5].getY(), (int) upInfoRect[5].getWidth(), (int) upInfoRect[5].getHeight(), canva);


        upCardsFont = upCardsFont.deriveFont(getWidth() * 0.04f);
        g2D.setFont(upCardsFont);
        textHeight = (float) Registry.ffb.getStringBounds(g2D, String.valueOf(balls)).getHeight();
        upCmod = (int) (upInfoRect[0].getY() + (upInfoRect[0].getHeight() / 2) + textHeight / 6f);

        textWidth = (float) Registry.ffb.getStringBounds(g2D, String.valueOf(levelManager.getCurrentDethsCount())).getWidth();
        g2D.setColor(Color.DARK_GRAY);
        g2D.drawString(String.valueOf(levelManager.getCurrentDethsCount()), (float) (upInfoRect[1].getX() + upInfoRect[0].getWidth() / 2D - textWidth / 2D - 4), upCmod + 4);
        g2D.setColor(Color.CYAN);
        g2D.drawString(String.valueOf(levelManager.getCurrentDethsCount()), (float) (upInfoRect[1].getX() + upInfoRect[0].getWidth() / 2D - textWidth / 2D), upCmod);

        textWidth = (float) Registry.ffb.getStringBounds(g2D, String.valueOf(levelManager.getCurrentLifesCount())).getWidth();
        g2D.setColor(Color.DARK_GRAY);
        g2D.drawString(String.valueOf(levelManager.getCurrentLifesCount()), (float) (upInfoRect[3].getX() + (upInfoRect[0].getWidth() / 2D) - textWidth / 2D - 4), upCmod + 4);
        g2D.setColor(Color.CYAN);
        g2D.drawString(String.valueOf(levelManager.getCurrentLifesCount()), (float) (upInfoRect[3].getX() + (upInfoRect[0].getWidth() / 2D) - textWidth / 2D), upCmod);

        textWidth = (float) Registry.ffb.getStringBounds(g2D, String.valueOf(balls)).getWidth();
        g2D.setColor(Color.DARK_GRAY);
        g2D.drawString(String.valueOf(balls), (float) (upInfoRect[5].getX() + (upInfoRect[0].getWidth() / 2D) - textWidth / 2D - 4), upCmod + 4);
        g2D.setColor(Color.CYAN);
        g2D.drawString(String.valueOf(balls), (float) (upInfoRect[5].getX() + (upInfoRect[0].getWidth() / 2D) - textWidth / 2D), upCmod);
    }

    private void drawDownCards(Graphics2D g2D) {
        int countX = 9;
        int countY = 33;

        g2D.setFont(downCardsFont);
        if (levelManager.getTowersCount(0) > 0) {
            if (redTowerOver) {
                g2D.drawImage(cards[0], (int) downCardRect[0].getX(), (int) downCardRect[0].getY() - 3, (int) downCardRect[0].getWidth(), (int) downCardRect[0].getHeight(), canva);
                g2D.drawString(String.valueOf(levelManager.getTowersCount(0)), (int) downCardRect[0].getX() + countX, (int) downCardRect[0].getY() + countY - 3);
            } else {
                g2D.drawImage(cards[0], (int) downCardRect[0].getX(), (int) downCardRect[0].getY(), (int) downCardRect[0].getWidth(), (int) downCardRect[0].getHeight(), canva);
                g2D.drawString(String.valueOf(levelManager.getTowersCount(0)), (int) downCardRect[0].getX() + countX, (int) downCardRect[0].getY() + countY);
            }
        } else {
            g2D.drawImage(cards[4], (int) downCardRect[0].getX(), (int) downCardRect[0].getY(), (int) downCardRect[0].getWidth(), (int) downCardRect[0].getHeight(), canva);
        }

        if (levelManager.getTowersCount(1) > 0) {
            if (whiteTowerOver) {
                g2D.drawImage(cards[1], (int) downCardRect[1].getX(), (int) downCardRect[1].getY() - 3, (int) downCardRect[1].getWidth(), (int) downCardRect[1].getHeight(), canva);
                g2D.drawString(String.valueOf(levelManager.getTowersCount(1)), (int) downCardRect[1].getX() + countX, (int) downCardRect[1].getY() + countY - 3);
            } else {
                g2D.drawImage(cards[1], (int) downCardRect[1].getX(), (int) downCardRect[1].getY(), (int) downCardRect[1].getWidth(), (int) downCardRect[1].getHeight(), canva);
                g2D.drawString(String.valueOf(levelManager.getTowersCount(1)), (int) downCardRect[1].getX() + countX, (int) downCardRect[1].getY() + countY);
            }
        } else {
            g2D.drawImage(cards[5], (int) downCardRect[1].getX(), (int) downCardRect[1].getY(), (int) downCardRect[1].getWidth(), (int) downCardRect[1].getHeight(), canva);
        }

        if (levelManager.getTowersCount(2) > 0) {
            if (greenTowerOver) {
                g2D.drawImage(cards[2], (int) downCardRect[2].getX(), (int) downCardRect[2].getY() - 3, (int) downCardRect[2].getWidth(), (int) downCardRect[2].getHeight(), canva);
                g2D.drawString(String.valueOf(levelManager.getTowersCount(2)), (int) downCardRect[2].getX() + countX, (int) downCardRect[2].getY() + countY - 3);
            } else {
                g2D.drawImage(cards[2], (int) downCardRect[2].getX(), (int) downCardRect[2].getY(), (int) downCardRect[2].getWidth(), (int) downCardRect[2].getHeight(), canva);
                g2D.drawString(String.valueOf(levelManager.getTowersCount(2)), (int) downCardRect[2].getX() + countX, (int) downCardRect[2].getY() + countY);
            }
        } else {
            g2D.drawImage(cards[6], (int) downCardRect[2].getX(), (int) downCardRect[2].getY(), (int) downCardRect[2].getWidth(), (int) downCardRect[2].getHeight(), canva);
        }

        if (levelManager.getTowersCount(3) > 0) {
            if (mageTowerOver) {
                g2D.drawImage(cards[3], (int) downCardRect[3].getX(), (int) downCardRect[3].getY() - 3, (int) downCardRect[3].getWidth(), (int) downCardRect[3].getHeight(), canva);
                g2D.drawString(String.valueOf(levelManager.getTowersCount(3)), (int) downCardRect[3].getX() + countX, (int) downCardRect[3].getY() + countY - 3);
            } else {
                g2D.drawImage(cards[3], (int) downCardRect[3].getX(), (int) downCardRect[3].getY(), (int) downCardRect[3].getWidth(), (int) downCardRect[3].getHeight(), canva);
                g2D.drawString(String.valueOf(levelManager.getTowersCount(3)), (int) downCardRect[3].getX() + countX, (int) downCardRect[3].getY() + countY);
            }
        } else {
            g2D.drawImage(cards[7], (int) downCardRect[3].getX(), (int) downCardRect[3].getY(), (int) downCardRect[3].getWidth(), (int) downCardRect[3].getHeight(), canva);
        }
    }

    private void recheckCoordinatesByFrameResize() {
        levelManager.recheckData();

        try {
            int mww = mobWay == null ? -1 : mobWay.size();
            mobWay = levelManager.getCurrentMobWay();
            for (AbstractMob mob : levelManager.getCurrentMobs()) {
                mob.setWayXY(mobWay, mww, mobWay == null ? -1 : mobWay.size());
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

        towerPlaces = levelManager.getCurrentDeco(199);

        for (AbstractBuilding cbu : levelManager.getCurrentBuilds()) {
            if (cbu instanceof iTower) {
                cbu.resetSeakArea();
            }
        }
    }

    private void resetBrush() throws IOException {
        ItemsFrame.resetBrush();
        setCursor(FoxCursor.createCursor(Paths.get("cur0")));
        redTowerIsChosen = false;
        whiteTowerIsChosen = false;
        greenTowerIsChosen = false;
        mageTowerIsChosen = false;
    }

    private void setFullScreen(boolean flag) {
        dispose();

        if (flag) {
            Registry.userConf.setFullScreen(true);
            setUndecorated(true);
            setSize(Registry.screen);
        } else {
            Registry.userConf.setFullScreen(false);
            setUndecorated(false);
            setSize(FRAME_WIDTH, FRAME_HEIGHT);
        }

        setLocationRelativeTo(null);
        setVisible(true);

        canva.createBufferStrategy(2);
    }

    private boolean isCreativeMode() {
        return isCreativeMode;
    }

    private void rebuildRectangles() {
        isPaused = true;

        if (isCreativeMode()) {
            isPaused = false;
            return;
        }

        Double wPercent = getWidth() / 100D;
        Double hPercent = getHeight() / 100D;

        float plateWidth = (float) (wPercent * 5D);
        float upCmod = (float) (hPercent * 2D);

        upInfoRect[5] = new Rectangle2D.Float(getWidth() - plateWidth - 20, upCmod, plateWidth, plateWidth);
        upInfoRect[4] = new Rectangle2D.Float(getWidth() - plateWidth * 2 - 20, upCmod, plateWidth, plateWidth);

        upInfoRect[3] = new Rectangle2D.Float(getWidth() - plateWidth * 3 - 30, upCmod, plateWidth, plateWidth);
        upInfoRect[2] = new Rectangle2D.Float(getWidth() - plateWidth * 4 - 30, upCmod, plateWidth, plateWidth);

        upInfoRect[1] = new Rectangle2D.Float(getWidth() - plateWidth * 5 - 40, upCmod, plateWidth, plateWidth);
        upInfoRect[0] = new Rectangle2D.Float(getWidth() - plateWidth * 6 - 40, upCmod, plateWidth, plateWidth);


        plateWidth = (float) (wPercent * 7.5D);
        upCmod = getHeight() - 200;
        float wShift = (float) (wPercent * 1D);

        downCardRect[0] = new Rectangle2D.Float(wShift, upCmod, plateWidth, plateWidth * 1.5f);
        downCardRect[1] = new Rectangle2D.Float(wShift + plateWidth + 5f, upCmod, plateWidth, plateWidth * 1.5f);
        downCardRect[2] = new Rectangle2D.Float(wShift + (plateWidth * 2f) + 10f, upCmod, plateWidth, plateWidth * 1.5f);
        downCardRect[3] = new Rectangle2D.Float(wShift + (plateWidth * 3f) + 15f, upCmod, plateWidth, plateWidth * 1.5f);

        isPaused = false;
    }

    private void creativeClick(MouseEvent e) {
        if (itemsInFocus) {
            changeItemBrush(e);
        } else {
            // creating percents by point:
            if (ItemsFrame.freeMouse) {
                if (ItemsFrame.addPointToolIsSelected) {
                    ItemsFrame.addGreenKey(e.getPoint());
                } else if (ItemsFrame.remPointToolIsSelected) {
                    levelManager.removeObject(e.getPoint());
                } else if (ItemsFrame.defSquareToolIsSelected) {
                    System.out.println("ItemsFrame.defSquareToolIsSelected: " + ItemsFrame.defSquareToolIsSelected);
//					MapManager.getCurrentLevel().addBuild(new RedDefenceTower());
                    System.out.println("writing new Item: " + 199 + " by percentPoint " + e.getPoint());
                    levelManager.writeNewItemPercentPoint(199, e.getPoint());
                }
            } else {
                if (memoryDecorID > -1) {
                    System.out.println("mousePressed(): Adds new item to level #" + memoryDecorID);
                    levelManager.writeNewItemPercentPoint(memoryDecorID, e.getPoint());
                } else {
                    System.out.println("GameLevel (mousePressed): WARN: memoryDecorID = " + memoryDecorID);
                }
            }
        }
    }

    private void playClick(MouseEvent e) {
        if (!redTowerOver && !whiteTowerOver && !greenTowerOver && !mageTowerOver) {

            if (redTowerIsChosen) {
                for (AbstractDecor tPlace : towerPlaces) {
                    if (tPlace.getRectangle().contains(e.getPoint())) {
                        System.out.println("ставим red башню в точку " + e.getPoint() + " вместо " + tPlace);
                        levelManager.buildTower(tPlace, 0); // index "0" - it`s Red Tower.

                        redTowerIsChosen = false;
                        levelManager.decreaseTowerCount(0);
                        ItemsFrame.resetBrush();
                        break;
                    }
                }
            }

            if (whiteTowerIsChosen) {
                for (AbstractDecor tPlace : towerPlaces) {
                    if (tPlace.getRectangle().contains(e.getPoint())) {
                        System.out.println("ставим white башню в точку " + e.getPoint() + " вместо " + tPlace);
                        levelManager.buildTower(tPlace, 1); // index "1" - it`s White Tower.

                        whiteTowerIsChosen = false;
                        levelManager.decreaseTowerCount(1);
                        ItemsFrame.resetBrush();
                        break;
                    }
                }
            }

            if (greenTowerIsChosen) {
                for (AbstractDecor tPlace : towerPlaces) {
                    if (tPlace.getRectangle().contains(e.getPoint())) {
                        System.out.println("ставим green башню в точку " + e.getPoint() + " вместо " + tPlace);
                        levelManager.buildTower(tPlace, 2); // index "2" - it`s Green Tower.

                        greenTowerIsChosen = false;
                        levelManager.decreaseTowerCount(2);
                        ItemsFrame.resetBrush();
                        break;
                    }
                }
            }

            if (mageTowerIsChosen) {
                for (AbstractDecor tPlace : towerPlaces) {
                    if (tPlace.getRectangle().contains(e.getPoint())) {
                        System.out.println("ставим mage башню в точку " + e.getPoint() + " вместо " + tPlace);
                        levelManager.buildTower(tPlace, 3); // index "3" - it`s Mage Tower.

                        mageTowerIsChosen = false;
                        levelManager.decreaseTowerCount(3);
                        ItemsFrame.resetBrush();
                        break;
                    }
                }
            }

            /* If mouse over exist tower*/
            if (e.getClickCount() >= 2 && e.getButton() == 1) {
                for (AbstractBuilding build : levelManager.getCurrentBuilds()) {
                    if (build instanceof iTower) {
                        if (build.getRectangle().contains(e.getPoint())) {
                            levelManager.unbuildTower(build);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        BufferedImage tmp;
        iGameObject gob;
        Graphics2D g2d;

        if (redTowerOver && levelManager.getTowersCount(0) > 0 && !redTowerIsChosen) {
            setCursor(Registry.redTowerCur);
            redTowerIsChosen = true;

            gob = BuildBase.get(201, null);
            tmp = gob.getGhostImage();
            ghostImage = new BufferedImage((int) (tmp.getWidth() * (getCurrentBounds().getWidth() * gob.getScale())),
                    (int) (tmp.getHeight() * (getCurrentBounds().getWidth() * gob.getScale())), BufferedImage.TYPE_INT_ARGB);
            g2d = (Graphics2D) ghostImage.getGraphics();
            g2d.drawImage(tmp, 0, 0, ghostImage.getWidth(), ghostImage.getHeight(), null);
            g2d.dispose();
        } else {
            redTowerIsChosen = false;
        }

        if (whiteTowerOver && levelManager.getTowersCount(1) > 0 && !whiteTowerIsChosen) {
            setCursor(Registry.whiteTowerCur);
            whiteTowerIsChosen = true;

            gob = BuildBase.get(202, null);
            tmp = gob.getGhostImage();
            ghostImage = new BufferedImage((int) (tmp.getWidth() * (getCurrentBounds().getWidth() * gob.getScale())),
                    (int) (tmp.getHeight() * (getCurrentBounds().getWidth() * gob.getScale())), BufferedImage.TYPE_INT_ARGB);
            g2d = (Graphics2D) ghostImage.getGraphics();
            g2d.drawImage(tmp, 0, 0, ghostImage.getWidth(), ghostImage.getHeight(), null);
            g2d.dispose();
        } else {
            whiteTowerIsChosen = false;
        }

        if (greenTowerOver && levelManager.getTowersCount(2) > 0 && !greenTowerIsChosen) {
            setCursor(Registry.greenTowerCur);
            greenTowerIsChosen = true;

            gob = BuildBase.get(203, null);
            tmp = gob.getGhostImage();
            ghostImage = new BufferedImage((int) (tmp.getWidth() * (getCurrentBounds().getWidth() * gob.getScale())),
                    (int) (tmp.getHeight() * (getCurrentBounds().getWidth() * gob.getScale())), BufferedImage.TYPE_INT_ARGB);
            g2d = (Graphics2D) ghostImage.getGraphics();
            g2d.drawImage(tmp, 0, 0, ghostImage.getWidth(), ghostImage.getHeight(), null);
            g2d.dispose();
        } else {
            greenTowerIsChosen = false;
        }

        if (mageTowerOver && levelManager.getTowersCount(3) > 0 && !mageTowerIsChosen) {
            setCursor(Registry.mageTowerCur);
            mageTowerIsChosen = true;

            gob = BuildBase.get(204, null);
            tmp = gob.getGhostImage();
            ghostImage = new BufferedImage((int) (tmp.getWidth() * (getCurrentBounds().getWidth() * gob.getScale())),
                    (int) (tmp.getHeight() * (getCurrentBounds().getWidth() * gob.getScale())), BufferedImage.TYPE_INT_ARGB);
            g2d = (Graphics2D) ghostImage.getGraphics();
            g2d.drawImage(tmp, 0, 0, ghostImage.getWidth(), ghostImage.getHeight(), null);
            g2d.dispose();
        } else {
            mageTowerIsChosen = false;
        }
    }

    // LISTENERS:
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 3) {
            try {
                resetBrush();
            } catch (IOException ex) {
                log.error("Error here: {}", ex.getMessage());
            }
        } else {
            if (isCreativeMode()) {
                creativeClick(e);
            } else {
                playClick(e);
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (isCreativeMode()) {
            Component comp = (Component) e.getSource();
            if (comp.getName() != null) {
                if (comp.getName().startsWith("itemButton_")) {
//					System.out.println("Over in: " + comp.getName());
                    itemsInFocus = true;
                    getContentPane().setBackground(Color.DARK_GRAY);
                }
            }

            selectObject(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (isCreativeMode()) {
            Component comp = (Component) e.getSource();
            if (comp.getName() != null) {
                if (comp.getName().startsWith("itemButton_")) {
                    itemsInFocus = false;
                    getContentPane().setBackground(Color.GRAY);
                }
            }

            deselectObject(e);
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        rebuildRectangles();
        recheckCoordinatesByFrameResize();
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        isPaused = true;
    }

    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        isStoryPlayed = false;
        log.info("GameLevel frame is closing. Exit back to menu...");
        SavesEngine.saveAll();
        if (itemsFrame != null) {
            if (itemsFrame.isVisible()) {
                itemsFrame.dispose();
            }
        }
        dispose();
        new StartMenu();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePoint = e.getPoint();

        if (isCreativeMode || downCardRect[0] == null) {
            return;
        }

        redTowerOver = downCardRect[0].contains(e.getPoint());

        whiteTowerOver = downCardRect[1].contains(e.getPoint());

        greenTowerOver = downCardRect[2].contains(e.getPoint());

        mageTowerOver = downCardRect[3].contains(e.getPoint());
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        if (e.getNewState() == 6) {
            setFullScreen(true);
        } else if (e.getNewState() == 0) {
            setFullScreen(false);
        } else {
            System.err.println("GameFrame: Unhandled windows state: " + e.getNewState());
        }
    }
}
