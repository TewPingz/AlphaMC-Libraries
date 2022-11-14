package rip.alpha.libraries.command.context.impl.bukkit;

import org.bukkit.entity.EntityType;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;
import rip.alpha.libraries.util.message.MessageColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityTypeContext implements ContextResolver<EntityType> {

    private final Set<EntityType> whitelistedTypes;
    private final List<String> tabComplete;
    private final String errorMessage;

    public EntityTypeContext() {
        this.whitelistedTypes = new HashSet<>();
        this.tabComplete = new ArrayList<>();

        for (EntityType entityType : EntityType.values()) {
            if (entityType.isAlive() && entityType.isSpawnable()) {
                this.whitelistedTypes.add(entityType);
            }
        }

        for (EntityType type : this.whitelistedTypes) {
            this.tabComplete.add(type.name());
        }

        this.errorMessage = MessageColor.RED + "That is an invalid entity type";
    }

    @Override
    public EntityType resolve(ArgumentContext<EntityType> input) {
        try {
            EntityType type = EntityType.valueOf(input.input().toUpperCase());

            if (!this.whitelistedTypes.contains(type)) {
                input.sender().sendMessage(this.errorMessage);
                return null;
            }

            return type;
        } catch (Exception e) {
            input.sender().sendMessage(this.errorMessage);
            return null;
        }
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<EntityType> context) {
        return this.tabComplete;
    }
}
