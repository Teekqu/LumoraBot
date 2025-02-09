package eu.devload.twitch.objects;

import com.github.twitch4j.common.enums.AnnouncementColor;
import com.github.twitch4j.helix.domain.BanUserInput;
import com.github.twitch4j.helix.domain.ChannelEditorList;
import com.github.twitch4j.helix.domain.ChannelInformation;
import com.github.twitch4j.helix.domain.InboundFollowers;
import com.github.twitch4j.helix.domain.Moderator;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.SubscriptionList;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.utils.Convert;
import eu.devload.twitch.utils.SystemAPI;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TwitchChannel {

    private final String id;

    public TwitchChannel(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }

    public String oauth2() {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM OauthTokens WHERE id='" + id + "';");
            if (!rs.next()) {
                try {
                    rs.close();
                } catch (Exception ignored) {
                }
                SystemAPI.get().twitchManager().leaveChannel(this);
                return null;
            }
            String accessToken = rs.getString("token");
            String refreshToken = rs.getString("refreshToken");
            long expires = rs.getLong("expiresIn");
            long generated = rs.getLong("generated");
            try {
                rs.close();
            } catch (Exception ignored) {
            }

            String oauth2;
            if (System.currentTimeMillis() / 1000 > generated + expires) {
                JSONObject json = SystemAPI.get().twitchManager().generateNewOauth2TokenWithRefreshToken(refreshToken);
                if (json == null) {
                    SystemAPI.get().twitchManager().removeChannel(this);
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

    public boolean exists() {
        return this.oauth2() != null;
    }

    public UserObject getUser() {
        return CacheManager.get().getUserById(this.id);
    }

    public String getName() {
        return this.getUser().login();
    }

    public String getDisplayName() {
        return this.getUser().displayName();
    }

    public String getProfileImageUrl() {
        return this.getUser().profileImageUrl();
    }

    public String getBroadcasterType() {
        return this.getUser().broadcasterType();
    }

    public String getDescription() {
        return this.getUser().description();
    }

    public int getSubCount() {
        return (SystemAPI.get().client().getHelix().getSubscriptions(this.oauth2(), this.id, null, null, null).execute()).getTotal();
    }

    public int getFollowers() {
        return (SystemAPI.get().client().getHelix().getChannelFollowers(this.oauth2(), this.id, null, null, null).execute()).getTotal();
    }

    public String getUptime() {
        StreamList streams = SystemAPI.get().client().getHelix().getStreams(this.oauth2(), null, null, 1, null, null, Collections.singletonList(this.id), null).execute();
        return streams != null && !streams.getStreams().isEmpty() ? Convert.secondsToFormat((streams.getStreams().getFirst()).getUptime().toSeconds()) : "-1s";
    }

    public boolean isBroadcaster(String userId) {
        return this.id.equals(userId);
    }

    public boolean isModerator(String userId) {
        if (this.isBroadcaster(userId)) return true;
        List<Moderator> mods = (SystemAPI.get().client().getHelix().getModerators(this.oauth2(), this.id, null, null, null).execute()).getModerators();
        return mods.stream().anyMatch((e) -> {
            return e.getUserId().equals(userId);
        });
    }

    public boolean isEditor(String userId) {
        if (this.isBroadcaster(userId)) return true;

        ChannelEditorList editors = SystemAPI.get().client().getHelix().getChannelEditors(this.oauth2(), this.id).execute();
        return editors != null && !editors.getEditors().isEmpty() && editors.getEditors().stream().anyMatch((e) -> {
            return e.getUserId().equals(userId);
        });

    }

    public boolean isSubscriber(String userId) {
        if (this.isBroadcaster(userId)) return true;

        SubscriptionList subs = SystemAPI.get().client().getHelix().getSubscriptions(this.oauth2(), this.id, null, null, null).execute();
        return subs != null && !subs.getSubscriptions().isEmpty() && subs.getSubscriptions().stream().anyMatch((e) -> {
            return e.getUserId().equals(userId);
        });
    }

    public String getFollowAge(String userId) {
        if (this.id.equals(userId))
            return Convert.secondsToFormat(System.currentTimeMillis() / 1000L - this.getUser().createdAt());
        InboundFollowers follows = SystemAPI.get().client().getHelix().getChannelFollowers(this.oauth2(), this.id, userId, null, null).execute();
        return follows != null && follows.getFollows() != null && !follows.getFollows().isEmpty() ? Convert.secondsToFormat(System.currentTimeMillis() / 1000L - (follows.getFollows().getFirst()).getFollowedAt().getEpochSecond()) : "-1";
    }

    public boolean isLive() {
        return !this.getUptime().equals("-1s");
    }

    public Stream getStream() {
        StreamList streams = SystemAPI.get().client().getHelix().getStreams(this.oauth2(), null, null, 1, null, null, Collections.singletonList(this.id), null).execute();
        return streams != null && !streams.getStreams().isEmpty() ? streams.getStreams().getFirst() : null;
    }

    public void sendMessage(String message) {
        SystemAPI.get().client().getChat().sendMessage(this.getName(), message);
    }

    public void timeoutUser(String userId, int seconds, String reason) {
        CompletableFuture.runAsync(() -> {
            if(this.isBroadcaster(userId)) return;
            if(this.isEditor(userId)) return;
            if(this.isModerator(userId)) return;
            BanUserInput banUserInput = BanUserInput.builder().userId(userId).reason(reason).duration(seconds).build();
            SystemAPI.get().client().getHelix().banUser(this.oauth2(), this.id, this.id, banUserInput).execute();
        });
    }

    public void banUser(String userId, String reason) {
        CompletableFuture.runAsync(() -> {
            BanUserInput banUserInput = BanUserInput.builder().userId(userId).reason(reason).build();
            SystemAPI.get().client().getHelix().banUser(this.oauth2(), this.id, this.id, banUserInput).execute();
        });
    }

    public void unbanUser(String userId) {
        CompletableFuture.runAsync(() -> {
            SystemAPI.get().client().getHelix().unbanUser(this.oauth2(), this.id, this.id, userId).execute();
        });
    }

    public void setTitle(String title) {
        CompletableFuture.runAsync(() -> {
            ChannelInformation channelInformation = ChannelInformation.builder().title(title).build();
            SystemAPI.get().client().getHelix().updateChannelInformation(this.oauth2(), this.id, channelInformation).execute();
        });
    }

    public void setGame(String game) {
        CompletableFuture.runAsync(() -> {
            ChannelInformation channelInformation = ChannelInformation.builder().gameName(game).build();
            SystemAPI.get().client().getHelix().updateChannelInformation(this.oauth2(), this.id, channelInformation).execute();
        });
    }

    public void sendAnnouncement(String message) {
        CompletableFuture.runAsync(() -> {
            SystemAPI.get().client().getHelix().sendChatAnnouncement(this.oauth2(), this.id, this.id, message, AnnouncementColor.PRIMARY).execute();
        });
    }

    public List<String> getChatters() {
        try {
            URL url = new URL("https://api.twitch.tv/helix/chat/chatters?broadcaster_id=" + this.id + "&moderator_id=" + this.id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Client-ID", (String) SystemAPI.get().config().get("twitch.client.id"));
            conn.setRequestProperty("Authorization", "Bearer " + this.oauth2());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            StringBuilder sb = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }

            in.close();
            conn.disconnect();
            List<String> chatters = new ArrayList<>();
            if (sb.toString().isEmpty()) return chatters;
            JSONObject json = new JSONObject(sb.toString());
            if (json.isEmpty() || json.getJSONArray("data").isEmpty()) return chatters;
            for (Object object : json.getJSONArray("data")) {
                JSONObject jo = (JSONObject) object;
                chatters.add(jo.getString("user_id"));
            }

            return chatters;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public boolean leave() {
        return SystemAPI.get().twitchManager().removeChannel(this);
    }

}
