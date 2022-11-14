package rip.alpha.libraries.util.data;

import com.google.gson.JsonObject;
import rip.alpha.libraries.json.GsonProvider;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class MojangRequest {

    private static final String ID_TO_NAME_ENDPOINT = "https://api.mojang.com/user/profile/%s";
    private static final String NAME_TO_ID_ENDPOINT = "https://api.mojang.com/users/profiles/minecraft/%s";

    public static String fetchName(UUID playerID) {
        JsonObject object = requestRest(URI.create(ID_TO_NAME_ENDPOINT.formatted(playerID)));
        return object == null ? null : object.get("name").getAsString();
    }

    public static UUID fetchID(String playerName) {
        JsonObject object = requestRest(URI.create(NAME_TO_ID_ENDPOINT.formatted(playerName)));
        return object == null ? null : fromDashLes(object.get("id").getAsString());
    }

    private static JsonObject requestRest(URI uri) {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder(uri)
                .GET()
                .header("accept", "application/json")
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response == null || response.statusCode() != 200) {
            return null;
        }
        return GsonProvider.fromJson(response.body(), JsonObject.class);
    }

    private static UUID fromDashLes(String dashLesUuid) {
        return UUID.fromString(dashLesUuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }
}
