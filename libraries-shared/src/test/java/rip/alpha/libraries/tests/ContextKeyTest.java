package rip.alpha.libraries.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.reflect.TypeToken;
import rip.alpha.libraries.Libraries;
import rip.alpha.libraries.json.GsonProvider;
import rip.alpha.libraries.model.ContextKey;
import rip.alpha.libraries.model.DomainContext;

import java.util.UUID;

public class ContextKeyTest {

    @BeforeAll
    public static void setup() {
        Libraries.getInstance().registerSerializers();
    }

    @Test
    public void contextKeyEqualityTest() {
        DomainContext<String, UUID> domainContext = DomainContext.<String, UUID>builder()
                .keyClass(String.class)
                .valueClass(UUID.class)
                .namespace("test-namespace")
                .build();
        ContextKey<String, UUID> key = domainContext.asKey();

        Assertions.assertEquals(domainContext, key);
        Assertions.assertEquals(domainContext.hashCode(), key.hashCode());

        String json = GsonProvider.toJson(key);
        ContextKey<String, UUID> deserialized = GsonProvider.fromJson(json, new TypeToken<ContextKey<String, UUID>>() {
        }.getType());

        Assertions.assertEquals(key, deserialized);
        Assertions.assertEquals(key.hashCode(), deserialized.hashCode());

        Assertions.assertEquals(domainContext, deserialized);
        Assertions.assertEquals(domainContext.hashCode(), deserialized.hashCode());

        ContextKey<?, ?> deserializedObj = GsonProvider.fromJson(json, ContextKey.class);

        Assertions.assertEquals(key, deserializedObj);
        Assertions.assertEquals(key.hashCode(), deserializedObj.hashCode());
    }

}
