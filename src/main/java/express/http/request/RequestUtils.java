package express.http.request;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import express.http.Cookie;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;

final class RequestUtils {

    private RequestUtils() {}

    /**
     * Extract the cookies from the 'Cookie' header.
     *
     * @param headers The Headers
     * @return An HashMap with the cookie name as key and the complete cookie as value.
     */
    static HashMap<String, Cookie> parseCookies(Headers headers) {
        HashMap<String, Cookie> cookieList = new HashMap<>();
        List<String> headerCookies = headers.get("Cookie");

        if (headerCookies == null || headerCookies.isEmpty()) {
            return cookieList;
        }

        char[] chars = headerCookies.get(0).toCharArray();
        StringBuilder key = new StringBuilder();
        StringBuilder val = new StringBuilder();
        boolean swap = false;

        for (char c : chars) {
            if (c == '=') {
                swap = true;
            } else if (c == ';') {
                String rkey = key.toString().trim();
                cookieList.put(rkey, new Cookie(rkey, val.toString()));

                key.setLength(0);
                val.setLength(0);
                swap = false;
            } else if (swap) {
                val.append(c);
            } else {
                key.append(c);
            }
        }

        if (key.length() > 0 && val.length() > 0) {
            String rkey = key.toString().trim();
            cookieList.put(rkey, new Cookie(rkey, val.toString()));
        }

        return cookieList;
    }

    /**
     * Method to extract the query's from an url.
     *
     * @param rawQuery The raw query
     * @return An list with key-values which are encoded in UTF8.
     */
    static HashMap<String, String> parseRawQuery(String rawQuery) {
        HashMap<String, String> querys = new HashMap<>();

        // Return empty map on null
        if (rawQuery == null) {
            return querys;
        }

        StringBuilder key = new StringBuilder();
        StringBuilder val = new StringBuilder();
        char[] chars = rawQuery.toCharArray();
        boolean keyac = false;
        char c = '=';

        for (char cc : chars) {
            c = cc;

            if (c == '=') {
                keyac = true;
            } else if (c == '&') {

                try {
                    querys.put(URLDecoder.decode(key.toString(), "UTF-8"), URLDecoder.decode(val.toString(), "UTF8"));
                } catch (UnsupportedEncodingException ignored) {
                }

                key.setLength(0);
                val.setLength(0);
                keyac = false;
            } else if (keyac) {
                val.append(c);
            } else {
                key.append(c);
            }
        }

        if (c != '=' && c != '&') {
            querys.put(key.toString(), val.toString());
        }

        return querys;
    }

    public static Object convertBodyToObject(InputStream is, Class klass) {
        String body = convertBodyToJson(is);
        return new Gson().fromJson(body, klass);
    }

    public static String convertBodyToJson(InputStream is) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
