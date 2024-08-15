package game.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class GameConfiguration {

    @Builder.Default
    private boolean showFps = false;

    @Builder.Default
    private boolean showLogo = true;

    @Builder.Default
    private boolean isLogEnabled = true;

    @Builder.Default
    private boolean useMods = false;

    private String lastUserName;

    private int lastUserHash;
}
