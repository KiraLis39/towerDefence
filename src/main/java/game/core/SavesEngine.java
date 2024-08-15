package game.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import game.config.GameConfiguration;
import game.config.Registry;
import game.config.UserConf;
import game.config.UserSave;
import game.levels.Level;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SavesEngine {

    public static void save(GameConfiguration configuration) {
        try {
            new ObjectMapper().writeValue(Registry.gameConfigFilePath, configuration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameConfiguration loadOrCreateGameConfig(File file) {
        try {
            if (Files.notExists(Path.of(file.toURI()))) {
                return GameConfiguration.builder().build();
            }
            return new ObjectMapper().readValue(file, GameConfiguration.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(UserConf userConf) {
        try {
            new ObjectMapper().writeValue(Registry.playerConfigFilePath, userConf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static UserConf loadOrCreateUserConfig(File file) {
        try {
            if (Files.notExists(Path.of(file.toURI()))) {
                return UserConf.builder().build();
            }
            return new ObjectMapper().readValue(file, UserConf.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(UserSave userSave) {
        try {
            new ObjectMapper().writeValue(Registry.playerSaveFilePath, userSave);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(Level level) {
        try {
            new ObjectMapper().writeValue(new File(Registry.levelsDir + "/" + level.getLevelName() + ".map"), level);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static UserSave loadOrCreateUserSave(File file) {
        try {
            if (Files.notExists(Path.of(file.toURI()))) {
                return UserSave.builder()
                        .name(Registry.userConf.getUserName())
                        .build();
            }
            return new ObjectMapper().readValue(file, UserSave.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveAll() {
        try {
            new ObjectMapper().writeValue(Registry.playerSaveFilePath, Registry.userSave);
            new ObjectMapper().writeValue(Registry.playerConfigFilePath, Registry.userConf);
            new ObjectMapper().writeValue(Registry.gameConfigFilePath, Registry.gameConfiguration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
