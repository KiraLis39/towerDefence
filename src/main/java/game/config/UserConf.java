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
    @Builder.Default
    public boolean isUseBicubic = true;
    @Builder.Default
    public boolean isDebugMode = true;
    private String userName;
    @Builder.Default
    private volatile FoxRender.RENDER quality = FoxRender.RENDER.MED;
    @Builder.Default
    private volatile boolean isFullScreen = true;
    @Builder.Default
    private volatile boolean showLogo = true;
    @Builder.Default
    private volatile int musicVolume = 100;
    @Builder.Default
    private volatile boolean musicMuted = false;
    @Builder.Default
    private volatile int soundVolume = 100;
    @Builder.Default
    private volatile boolean soundMuted = false;
    @Builder.Default
    private boolean isCreativeMode = false;
    @Builder.Default
    private boolean useSmoothing = true;

    public void nextQuality() {
        int curQ = quality.ordinal();
        quality = FoxRender.RENDER.values().length > curQ + 1 ? FoxRender.RENDER.values()[curQ + 1] : FoxRender.RENDER.values()[0];
    }
}
