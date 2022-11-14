package rip.alpha.libraries.command.context.impl.java;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.type.ArgumentContext;
import rip.alpha.libraries.command.context.type.TabCompleteArgumentContext;
import rip.alpha.libraries.util.message.MessageColor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("ALL")
public class EnumContext implements ContextResolver<Enum> {

    private Map<Class<? extends Enum>, EnumCollectionEntry> enumCache = new ConcurrentHashMap<>();

    @Override
    public Enum resolve(ArgumentContext<Enum> context) {
        EnumCollectionEntry entry = this.getEnums(context.clazz());
        Enum anEnum = entry.getEnum(context.input().toLowerCase());

        if (anEnum == null) {
            context.sender().sendMessage(MessageColor.RED + "There is no such thing as " + context.input() + " for " + context.clazz().getSimpleName());
            context.sender().sendMessage(MessageColor.RED + "e.g. " + entry.getKeySet().get(0));
            return null;
        }

        return anEnum;
    }

    @Override
    public List<String> getTabComplete(TabCompleteArgumentContext<Enum> context) {
        return this.getEnums(context.clazz()).getKeySet();
    }

    private EnumCollectionEntry getEnums(Class<? extends Enum> clazz) {
        return this.enumCache.computeIfAbsent(clazz, aClass -> {
            EnumCollectionEntry entry = new EnumCollectionEntry();
            EnumSet<?> enums = EnumSet.allOf(aClass);
            for (Enum<?> anEnum : enums) {
                entry.addEntry(anEnum);
            }
            return entry;
        });
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private class EnumCollectionEntry {
        private final Map<String, Enum> enumMap = new HashMap<>();

        private void addEntry(Enum anEnum) {
            this.enumMap.put(anEnum.name().toLowerCase(), anEnum);
        }

        private Enum getEnum(String name) {
            return this.enumMap.get(name.toLowerCase());
        }

        private Collection<Enum> getEnums() {
            return Collections.unmodifiableCollection(this.enumMap.values());
        }

        private List<String> getKeySet() {
            return new ArrayList<>(this.enumMap.keySet());
        }
    }
}
