package game.config;

import fox.FoxFontBuilder;
import fox.images.FoxCursor;
import fox.images.FoxSpritesCombiner;
import fox.utils.InputAction;
import fox.utils.MediaCache;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@Slf4j
@Getter
public final class Registry {
    public static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    public static final String verse = "0.0.9.2";

    public static final Charset charset = StandardCharsets.UTF_8;
    public static final InputAction inAc = new InputAction();
    public static final MediaCache cache = new MediaCache().getInstance();
    public static final FoxFontBuilder ffb = new FoxFontBuilder();
    public static final FoxSpritesCombiner sComb = new FoxSpritesCombiner();
    public static final File gameConfigFilePath = new File("./config.json");
    public static final File playerConfigFilePath = new File("./player.json");
    public static final File playerSaveFilePath = new File("./save.json");
    public static final File levelsDir = new File("./level");
    public static final File defaultLevelFile = new File(levelsDir + "/0.map");
    public static GameConfiguration gameConfiguration;
    public static UserConf userConf;
    public static UserSave userSave;
    public static Cursor cur_0, addPoint, remPoint, defPoint, redTowerCur, whiteTowerCur, greenTowerCur, mageTowerCur;

    static {
        try {
            cur_0 = FoxCursor.createCursor(Paths.get("cur0"));
            addPoint = FoxCursor.createCursor(Paths.get("addPoint"));
            remPoint = FoxCursor.createCursor(Paths.get("remPoint"));

            defPoint = FoxCursor.createCursor(Paths.get("defPoint"));
            redTowerCur = FoxCursor.createCursor(Paths.get("redTowerCur"));
            whiteTowerCur = FoxCursor.createCursor(Paths.get("whiteTowerCur"));
            greenTowerCur = FoxCursor.createCursor(Paths.get("greenTowerCur"));
            mageTowerCur = FoxCursor.createCursor(Paths.get("mageTowerCur"));
        } catch (IOException e) {
            log.error("Error: {}", e.getMessage());
        }
    }
}
