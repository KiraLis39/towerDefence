package game.config;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class FoxAudioProcessor {
    private static final Map<String, byte[]> musicMap = new LinkedHashMap<>();
    private static final Map<String, byte[]> soundMap = new LinkedHashMap<>();
    private static JavaSoundAudioDevice auDevMusic;
    private static AdvancedPlayer musicPlayer;
    private static Player soundPlayer;

    @Setter
    private static boolean soundMuted = false, musicMuted = false;

    public static void addSound(String name, String path) throws IOException, URISyntaxException {
        URL url = FoxAudioProcessor.class.getResource(path);
        assert url != null;
        soundMap.put(name, Files.readAllBytes(Path.of(url.toURI())));
    }

    public static void addMusic(String name, String path) throws IOException, URISyntaxException {
        URL url = FoxAudioProcessor.class.getResource(path);
        assert url != null;
        musicMap.put(name, Files.readAllBytes(Path.of(url.toURI())));
    }

    public static void playSound(String trackName) {
        if (soundMuted) {
            return;
        }

        if (soundMap.containsKey(trackName)) {
            new Thread(() -> {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(soundMap.get(trackName))) {
                    soundPlayer = new Player(bais);
                    soundPlayer.play();
                } catch (IOException | JavaLayerException e) {
                    log.error("Error: {}", e.getMessage());
                }

//					javafx.scene.media.Media sound = new javafx.scene.media.Media(soundMap.get(trackName).toURI().toString());
//					MediaPlayer soundPlayer = new MediaPlayer(sound);
//			        soundPlayer.setVolume(gVolume);
//			        soundPlayer.play();
            }).start();
        }
    }

    public static void playMusic(String trackName, Boolean rep) {
//		System.out.println("Into playMusic...");
        if (musicMuted) {
            return;
        }

//		System.out.println("chek music existing...");
        if (musicMap.containsKey(trackName)) {
            stopMusic();

            // wav file playes
//		    AudioClip audioClip = AppletManager.applet.getAudioClip(soundFile.toURL());
//		    audioClip.play();
//			System.out.println("start new music-thread...");
            new Thread(() -> {
//					try (ByteArrayInputStream bais = new ByteArrayInputStream(soundMap.get(trackName))) {
//						musicPlayer2 = new Player(bais);
//						musicPlayer2.play();
//					} catch (IOException | JavaLayerException e) {
//					    log.error("Error: {}", e.getMessage());
//					} finally {
//					    musicPlayer2.close();
//					}
                auDevMusic = new JavaSoundAudioDevice();
                try (ByteArrayInputStream bais = new ByteArrayInputStream(soundMap.get(trackName))) {
                    auDevMusic.setLineGain(0.75f);
                    musicPlayer = new AdvancedPlayer(bais, auDevMusic);
                    PlaybackListener listener = new PlaybackListener() {
                        @Override
                        public void playbackStarted(PlaybackEvent arg0) {
                            System.out.println("Playback started..");
                        }

                        @Override
                        public void playbackFinished(PlaybackEvent event) {
                            System.out.println("Playback finished..");
                        }
                    };
                    musicPlayer.setPlayBackListener(listener);
                    musicPlayer.play();
                } catch (Exception e) {
                    log.error("Error: {}", e.getMessage());
                }
            }).start();

            log.info("Media: music: the '" + trackName + "' exist into musicMap and play now...");
        } else {
            log.info("Media: music: music '" + trackName + "' is NOT exist in the musicMap");
        }
    }

    public static void nextMusic() {
        int playingNowCount = new Random().nextInt(musicMap.size());
        System.out.println("Music maps sise: " + musicMap.size() + ". random count is: " + playingNowCount);

        int tmpCount = 0;
        for (String musikName : musicMap.keySet()) {
            if (tmpCount == playingNowCount) {
                playMusic(musikName, true);
                break;
            }
            tmpCount++;
        }
    }

    public static void stopMusic() {
//		for (Entry<String, OggClip> iterable_element : musicMap.entrySet()) {
//		if (iterable_element.getValue() == null) {
//			log.info(Media.class, 2, "iterable_element into musicMap is NULL. Returning...");
//			return;
//		}
//
//		try {iterable_element.getValue().stop();} catch (Exception e) {/* IGNORE */}
        try {
            musicPlayer.stop();
//			musicPlayer2.close();
        } catch (Exception e) {/* IGNORE */}
    }

    public static void removeMusic(String key) {
        musicMap.remove(key);
    }
}
