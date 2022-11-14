package rip.alpha.libraries.skin;

import net.minecraft.util.com.mojang.authlib.properties.Property;

import java.util.UUID;

@SuppressWarnings("DuplicateStringLiteralInspection")
public record MojangSkin(UUID uuid, String value, String signature) {
    public Property toProperty() {
        return new Property("textures", this.value, this.signature);
    }
}
