package rip.alpha.libraries.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class URLUtils {

    public static String inputStreamToString(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toString("utf8");
        }
    }

    public static InputStream urlToInputStream(URL url, Map<String, String> args) {
        HttpURLConnection con;
        InputStream inputStream = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);
            if (args != null) {
                for (Map.Entry<String, String> e : args.entrySet()) {
                    con.setRequestProperty(e.getKey(), e.getValue());
                }
            }
            con.connect();
            inputStream = con.getInputStream();
            return inputStream;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
