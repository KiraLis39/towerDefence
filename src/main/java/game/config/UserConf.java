package game.config;

import fox.FoxRender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class UserConf {
    private String userName;

    private volatile FoxRender.RENDER quality;

    @Builder.Default
    private volatile boolean isFullScreen = true;

    @Builder.Default
    private volatile boolean showLogo = true;

    private volatile int musicVolume;

    private volatile boolean musicMuted;

    private volatile int soundVolume;

    private volatile boolean soundMuted;

    public void nextQuality() {
        int curQ = quality.ordinal();
        quality = FoxRender.RENDER.values().length > curQ + 1 ? FoxRender.RENDER.values()[curQ + 1] : FoxRender.RENDER.values()[0];
    }
}
