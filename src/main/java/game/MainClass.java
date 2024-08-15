package game;

import fox.FoxLogo;
import fox.utils.MediaCache;
import game.config.Registry;
import game.core.FoxAudioProcessor;
import game.core.SavesEngine;
import game.gui.StartMenu;
import lombok.extern.slf4j.Slf4j;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        checkConfigs();

        if (Registry.userConf.isShowLogo()) {
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

    private static void checkConfigs() {
        Registry.gameConfiguration = SavesEngine.loadOrCreateGameConfig(Registry.gameConfigFilePath);
        Registry.userConf = SavesEngine.loadOrCreateUserConfig(Registry.playerConfigFilePath);
        Registry.userSave = SavesEngine.loadOrCreateUserSave(Registry.playerSaveFilePath);
        FoxAudioProcessor.setMusicMuted(Registry.userConf.isMusicMuted());
        FoxAudioProcessor.setSoundMuted(Registry.userConf.isSoundMuted());
    }

    private static void showLogo() {
        alpha = 0f;

        try {
            Registry.cache.addLocalIfAbsent("logo", "/pic/gui/logo", MediaCache.DATA_TYPE.PNG);
        } catch (Exception e1) {
            log.error("Media loading error: {}", e1.getMessage());
        }


        try {
            loadMedia();
            loadResources();

            FoxLogo logo = new FoxLogo();
            logo.start("", FoxLogo.IMAGE_STYLE.WRAP, FoxLogo.BACK_STYLE.ASIS, Registry.cache.getBufferedImage("logo"));
            FoxAudioProcessor.addMusic("soundLogo", "/music/logo.mp3");
            logo.join(6_000);
            new StartMenu();
            logo.finalLogo();
            FoxAudioProcessor.removeMusic("soundLogo");
            Registry.cache.remove("logo");
        } catch (InterruptedException e) {
            log.error("Error: {}", e.getMessage());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadMedia() {
        log.info("Loading media files...");

        Registry.cache.addLocalIfAbsent("sButtonOver", "/sounds/sButtonOver", MediaCache.DATA_TYPE.MP3);
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

        log.info("Media files loaded.");
    }

    private static void loadResources() {
        log.info("Loading other resources...");
        try {
            // images:
            URL picsURL = MainClass.class.getResource("/pic/");
            assert picsURL != null;
            Path path = Path.of(picsURL.getPath().substring(1));
            try (Stream<Path> backgrounds = Files.walk(path)) {
                assert backgrounds != null;

                Set<String> images = backgrounds
                        .filter(file -> !Files.isDirectory(file))
                        .map(p -> p.toString().split("classes?")[1].substring(0, p.toString().split("classes?")[1].length() - 4))
                        .collect(Collectors.toSet());
                for (String background : images) {
                    Registry.cache.addLocalIfAbsent(
                            new File(background).getName(),
                            background,
                            MediaCache.DATA_TYPE.PNG);
                }

                log.info("Other resources loaded.");
            }
        } catch (Exception e) {
            log.error("Media loading error: {}", e.getMessage());
        }
    }
}
