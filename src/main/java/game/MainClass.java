package game;

import fox.utils.MediaCache;
import game.config.FoxAudioProcessor;
import game.config.Registry;
import game.config.SavesEngine;
import game.gui.StartMenu;
import lombok.extern.slf4j.Slf4j;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
public class MainClass {
    private static JLabel logoLabel;

    private static float alpha;


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception ex) {
            log.error("Look and feel error: {}", ex.getMessage());
        }

        checkDirectories();
        initIOM();

        if (Registry.userConf.isShowLogo()) {
            System.out.println("Wh logo");
            showLogo();
        }

        loadMedia();
        loadResources();

        new StartMenu();
    }

    private static void checkDirectories() {
        if (!Registry.levelsDir.exists()) {
            Registry.levelsDir.mkdirs();
        }
    }

    private static void initIOM() {
        Registry.gameConfiguration = SavesEngine.loadOrCreateGameConfig(Registry.gameConfigFilePath);
        Registry.userConf = SavesEngine.loadOrCreateUserConfig(Registry.playerConfigFilePath);
        Registry.userSave = SavesEngine.loadOrCreateUserSave(Registry.playerSaveFilePath);
        FoxAudioProcessor.setMusicMuted(Registry.userConf.isMusicMuted());
        FoxAudioProcessor.setSoundMuted(Registry.userConf.isSoundMuted());
    }

    private static void showLogo() {
        alpha = 0f;

        try {
            Registry.cache.addLocalIfAbsent("logo", "./resources/pic/game.gui/logo", MediaCache.DATA_TYPE.PNG);
        } catch (Exception e1) {
            log.error("Media loading error: {}", e1.getMessage());
        }

        BufferedImage logoImage = Registry.cache.getBufferedImage("start_logo");

        JFrame logoFrame = new JFrame() {
            {
                setUndecorated(true);
                setAlwaysOnTop(true);
                getContentPane().setBackground(Color.BLACK);
                setSize(Toolkit.getDefaultToolkit().getScreenSize());
                setLocationRelativeTo(null);
                setLayout(new BorderLayout());

                logoLabel = new JLabel() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2D = (Graphics2D) g;
                        if (alpha > 1f) {
                            alpha = 1f;
                        } else if (alpha < 0) {
                            alpha = 0;
                        }
                        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                        g2D.drawImage(logoImage, getWidth() / 2 - logoImage.getWidth() / 2, getHeight() / 2 - logoImage.getHeight() / 2, this);
                        g2D.dispose();
                    }
                };

                add(logoLabel);
            }
        };

        Thread logoThread = new Thread(() -> {
            try {
                FoxAudioProcessor.addMusic("soundLogo", "/music/logo.mp3");
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
            logoFrame.setVisible(true);

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                log.error("Error: {}", e.getMessage());
            }
            FoxAudioProcessor.playMusic("soundLogo", false);

            while (alpha < 1f) {
                alpha += 0.05f;
                logoLabel.repaint();

                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) {
                    log.error("Error: {}", e.getMessage());
                }
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.error("Error: {}", e.getMessage());
            }

            while (alpha > 0f) {
                alpha -= 0.025f;
                logoLabel.repaint();

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    log.error("Error: {}", e.getMessage());
                }
            }

            FoxAudioProcessor.removeMusic("soundLogo");
            Registry.cache.remove("start_logo");
        });
        logoThread.start();

        try {
            loadMedia();
            loadResources();

            logoThread.join();
            new StartMenu();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("Error: {}", e.getMessage());
            }
            logoFrame.dispose();
        } catch (InterruptedException e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    private static void loadMedia() {
        log.info("Loading media files...");

        Registry.cache.addLocalIfAbsent("sButtonOver", "/sounds/sButtonOver.mp3", MediaCache.DATA_TYPE.MP3);
        Registry.cache.addLocalIfAbsent("sButtonOver_2", "/sounds/sButtonOver_2", MediaCache.DATA_TYPE.MP3);
        Registry.cache.addLocalIfAbsent("sButtonPress", "/sounds/sButtonPress", MediaCache.DATA_TYPE.MP3);
        Registry.cache.addLocalIfAbsent("sFailSound", "/sounds/sFailSound", MediaCache.DATA_TYPE.MP3);

        Registry.cache.addLocalIfAbsent("sZombieSound_0", "/sounds/Zombie00", MediaCache.DATA_TYPE.MP3);
        Registry.cache.addLocalIfAbsent("sZombieSound_1", "/sounds/Zombie01", MediaCache.DATA_TYPE.MP3);
        Registry.cache.addLocalIfAbsent("sZombieSound_2", "/sounds/Zombie02", MediaCache.DATA_TYPE.MP3);
        Registry.cache.addLocalIfAbsent("sZombieSound_3", "/sounds/Zombie03", MediaCache.DATA_TYPE.MP3);
        Registry.cache.addLocalIfAbsent("sZombieSound_4", "/sounds/Zombie04", MediaCache.DATA_TYPE.MP3);
        Registry.cache.addLocalIfAbsent("sZombieSound_5", "/sounds/Zombie05", MediaCache.DATA_TYPE.MP3);

        Registry.cache.addLocalIfAbsent("RedTowerAttack", "/sounds/RedTowerAttack", MediaCache.DATA_TYPE.MP3);
        Registry.cache.addLocalIfAbsent("WhiteTowerAttack", "/sounds/WhiteTowerAttack", MediaCache.DATA_TYPE.MP3);
        Registry.cache.addLocalIfAbsent("GreenTowerAttack", "/sounds/GreenTowerAttack", MediaCache.DATA_TYPE.MP3);
        Registry.cache.addLocalIfAbsent("MageTowerAttack", "/sounds/MageTowerAttack", MediaCache.DATA_TYPE.MP3);

        Registry.cache.addLocalIfAbsent("destroyTower", "/sounds/DestroyTower", MediaCache.DATA_TYPE.MP3);
    }

    private static void loadResources() {
        log.info("Loading other resources...");
        try {
            // cursors:
            Registry.cache.addLocalIfAbsent("cur0", "/pic/cur/cur0", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("remPoint", "/pic/cur/remPoint", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("addPoint", "/pic/cur/addPoint", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("defPoint", "/pic/cur/defPoint", MediaCache.DATA_TYPE.PNG);

            Registry.cache.addLocalIfAbsent("redTowerCur", "/pic/cur/redTowerCur", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("whiteTowerCur", "/pic/cur/whiteTowerCur", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("greenTowerCur", "/pic/cur/greenTowerCur", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("mageTowerCur", "/pic/cur/mageTowerCur", MediaCache.DATA_TYPE.PNG);

            // base images:
            Registry.cache.addLocalIfAbsent("back", "/pic/game.gui/back", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("menuPic0", "/pic/game.gui/menuPic0", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("menuPic1", "/pic/game.gui/menuPic1", MediaCache.DATA_TYPE.PNG);

            // backgrounds:
            File[] backgrounds = new File("/pic/maps/").listFiles();
            assert backgrounds != null;
            for (File background : backgrounds) {
                Registry.cache.addLocalIfAbsent(background.getName().substring(4), background.getPath(), MediaCache.DATA_TYPE.PNG);
            }

            // sprite lists:
            Registry.cache.addLocalIfAbsent("mobSpawn_s", "/pic/game.mobs/mobSpawn_s", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("plop_s", "/pic/game.mobs/plop_s", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("flame_s", "/pic/game.mobs/melon/flame_s", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("melon_s", "/pic/game.mobs/melon/melon_s", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("zombie_s", "/pic/game.mobs/zombie/zombie_s", MediaCache.DATA_TYPE.PNG);

            Registry.cache.addLocalIfAbsent("tower_s", "/pic/game.buildings/tower_s", MediaCache.DATA_TYPE.PNG);

            Registry.cache.addLocalIfAbsent("clouds", "/pic/environment/clouds", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("decos", "/pic/environment/decos", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("decos_2", "/pic/environment/decos_2", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("decos_3", "/pic/environment/decos_3", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("grasslist", "/pic/environment/grasslist", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("up_tree", "/pic/environment/up_tree", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("big_tree", "/pic/environment/big_tree", MediaCache.DATA_TYPE.PNG);

            Registry.cache.addLocalIfAbsent("rubin_s", "/pic/game.buildings/rubin_s", MediaCache.DATA_TYPE.PNG);

            Registry.cache.addLocalIfAbsent("cards", "/pic/game.gui/cards/cards", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("buttons", "/pic/game.gui/info/buttons", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("button_default", "/pic/game.gui/info/button_default", MediaCache.DATA_TYPE.PNG);

            Registry.cache.addLocalIfAbsent("home", "/pic/game.buildings/home", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("redDefenceTower", "/pic/game.buildings/redDefenceTower", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("whiteDefenceTower", "/pic/game.buildings/whiteDefenceTower", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("greenDefenceTower", "/pic/game.buildings/greenDefenceTower", MediaCache.DATA_TYPE.PNG);
            Registry.cache.addLocalIfAbsent("mageDefenceTower", "/pic/game.buildings/mageDefenceTower", MediaCache.DATA_TYPE.PNG);
        } catch (Exception e) {
            log.error("Media loading error: {}", e.getMessage());
        }
    }
}
