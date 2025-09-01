package eu.devload.twitch.objects;

import eu.devload.twitch.utils.SystemAPI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.json.JSONObject;

import javax.swing.plaf.UIResource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Accessors(fluent = true)
public class ClientUser {

    public static ClientUser get() {
        return new ClientUser();
    }

    private final String id = "1210324229";
    private final String displayName = "LumoraBot";
    private final String login = displayName.toLowerCase();

    private JSONObject appToken = null;


    public String oauth() {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM OauthTokens WHERE id='" + id + "';");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                return null;
            }
            String accessToken = rs.getString("token");
            String refreshToken = rs.getString("refreshToken");
            long expires = rs.getLong("expiresIn");
            long generated = rs.getLong("generated");
            try { rs.close(); } catch (Exception ignored) { }

            String oauth2;
            if (System.currentTimeMillis() / 1000 > generated + expires) {
                JSONObject json = SystemAPI.get().twitchManager().generateNewOauth2TokenWithRefreshToken(refreshToken);
                if (json == null) {
                    return null;
                }
                String newOauth2 = json.getString("access_token");
                String newRefreshToken = json.getString("refresh_token");
                long expiresIn = json.getLong("expires_in");
                SystemAPI.get().database().execute("UPDATE OauthTokens SET token='" + newOauth2 + "', refreshToken='" + newRefreshToken + "', expiresIn=" + expiresIn + ", generated=" + System.currentTimeMillis() / 1000 + " WHERE id=" + id);
                oauth2 = newOauth2;
                return oauth2;
            }
            oauth2 = accessToken;
            return oauth2;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public String appToken() {
        if(this.appToken == null) {
            this.appToken = this.generateNewAppToken();
        }
        if(this.appToken.getLong("expires_in") + this.appToken.getLong("generated") < System.currentTimeMillis() / 1000) {
            this.appToken = this.generateNewAppToken();
        }
        return this.appToken.getString("access_token");
    }

    public JSONObject generateNewAppToken() {
        try {
            URL url = new URL(String.format("https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=client_credentials", SystemAPI.get().config().get("twitch.client.id"), SystemAPI.get().config().get("twitch.client.secret")));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }

            in.close();
            connection.disconnect();
            JSONObject json = new JSONObject(sb.toString());
            json.put("generated", System.currentTimeMillis() / 1000);
            return json;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

}
