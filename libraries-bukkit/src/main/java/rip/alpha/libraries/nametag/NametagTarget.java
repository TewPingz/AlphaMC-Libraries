package rip.alpha.libraries.nametag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NametagTarget {

    private String teamName;
    private String prefix;
    private String suffix;
    private boolean friendly;
    private NametagVisibility visibility;

}
