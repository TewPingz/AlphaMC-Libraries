package rip.alpha.libraries.nametag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NametagVisibility {

    ALWAYS("always"),
    HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
    HIDE_FOR_OWN_TEAM("hideForOwnTeam"),
    NEVER("never");

    private final String nmsName;

}
