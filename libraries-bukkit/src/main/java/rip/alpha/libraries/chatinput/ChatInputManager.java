package rip.alpha.libraries.chatinput;

import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatInputManager {

    @Getter
    private static final ChatInputManager instance = new ChatInputManager();

    private final Map<UUID, ChatInput> activeInputs = new ConcurrentHashMap<>();

    private ChatInputManager() {

    }

    public void addInput(ChatInput input) {
        this.activeInputs.put(input.getPlayerID(), input);
    }

    public ChatInput getInput(UUID playerID) {
        return this.activeInputs.get(playerID);
    }

    protected void removeInput(UUID playerID) {
        this.activeInputs.remove(playerID);
    }

}
