package eu.devload.twitch.modules.stats.utils;

import com.github.twitch4j.helix.domain.User;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.sql.ResultSet;
import java.util.HashMap;

public class StatsManager {

    public static void updateHighestSubCount(TwitchChannel ch, int newSubs, boolean check) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM HighestSubCount WHERE channelId='"+ch.getId()+"';");
            if (!rs.next()) {
                SystemAPI.get().database().execute("INSERT INTO HighestSubCount (channelId, count, time) VALUES ('" + ch.getId() + "', " + newSubs + ", " + (System.currentTimeMillis()/1000) + ");");
            } else {
                int subCount = rs.getInt("count");
                if (!check || newSubs > subCount) {
                    SystemAPI.get().database().execute("UPDATE HighestSubCount SET count=" + newSubs + ", time="+(System.currentTimeMillis()/1000)+" WHERE channelId='" + ch.getId() + "';");
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static int getHighestSubCount(TwitchChannel ch) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM HighestSubCount WHERE channelId='"+ch.getId()+"';");
            if (!rs.next()) {
                updateHighestSubCount(ch, ch.getSubCount(), false);
                try { rs.close(); } catch (Exception ignored) { }
                return ch.getSubCount();
            }
            int subCount = rs.getInt("count");
            try { rs.close(); } catch (Exception ignored) { }
            return subCount;
        } catch (Exception err) {
            err.printStackTrace();
            return 0;
        }
    }

    public static void updateMaximalWatchtime(TwitchChannel ch, int watchtime) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Watchtimes WHERE channelId='" + ch.getId() + "' AND userId='0';");
            if (!rs.next()) {
                SystemAPI.get().database().execute("INSERT INTO Watchtimes (channelId, userId, username, minutes) VALUES ('" + ch.getId() + "', '0', 'watchtime.max.value', '" + watchtime + "');");
            } else {
                long newMinutes = rs.getLong("minutes") + (long)watchtime;
                SystemAPI.get().database().execute("UPDATE Watchtimes SET minutes='" + newMinutes + "' WHERE channelId='" + ch.getId() + "' AND userId='0';");
            }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void startTemporaryStatistics(TwitchChannel ch, String topic) {
        if(topic.isEmpty()) topic = "Topic not set";
        try {
            SystemAPI.get().database().execute("DELETE FROM TemporaryStatistics WHERE channelId='" + ch.getId() + "';");
            SystemAPI.get().database().execute("INSERT INTO TemporaryStatistics (channelId, userId, username, messages, minutes, started) VALUES('" + ch.getId() + "', '0', '"+topic+"', 0, 0, " + (System.currentTimeMillis() / 1000L) + ");");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void addMaxTemporaryStatistics(TwitchChannel ch, int messages, int minutes) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM TemporaryStatistics WHERE channelId='" + ch.getId() + "' AND userId='0';");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                return;
            } else {
                int newMessages = rs.getInt("messages") + messages;
                int newMinutes = rs.getInt("minutes") + minutes;
                SystemAPI.get().database().execute("UPDATE TemporaryStatistics SET messages=" + newMessages + ", minutes=" + newMinutes + " WHERE channelId='" + ch.getId() + "' AND userId='0';");
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void addMessageCount(TwitchChannel channel, User user, int amount) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM MessageCounts WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
            if (!rs.next()) {
                SystemAPI.get().database().execute("INSERT INTO MessageCounts (channelId, userId, username, messageCount) VALUES ('" + channel.getId() + "', '" + user.getId() + "','" + user.getDisplayName() + "', " + amount + ");");
            } else {
                int newCount = Integer.parseInt(rs.getString("messageCount")) + amount;
                SystemAPI.get().database().execute("UPDATE MessageCounts SET messageCount=" + newCount + ", username='" + user.getDisplayName() + "' WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
            }
            try { rs.close(); } catch (Exception ignored) { }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void removeMessageCount(TwitchChannel channel, User user, int amount) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM MessageCounts WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
            if (!rs.next()) {
                try {
                    rs.close();
                } catch (Exception var6) {
                }

            } else {
                int count = Integer.parseInt(rs.getString("messageCount"));
                if (count - amount <= 0) {
                    SystemAPI.get().database().execute("DELETE FROM MessageCounts WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
                } else {
                    SystemAPI.get().database().execute("UPDATE MessageCounts SET messageCount=" + (count - amount) + ", username='" + user.getDisplayName() + "' WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
                }

                try { rs.close(); } catch (Exception ignored) { }

            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void resetMessageCount(TwitchChannel channel, User user) {
        try {
            SystemAPI.get().database().execute("DELETE FROM MessageCounts WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static int getMessageCount(TwitchChannel channel, User user) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM MessageCounts WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
            if (!rs.next()) {
                try {
                    rs.close();
                } catch (Exception var5) {
                }

                return 0;
            } else {
                int count = Integer.parseInt(rs.getString("messageCount"));

                try { rs.close(); } catch (Exception ignored) { }

                return count;
            }
        } catch (Exception err) {
            err.printStackTrace();
            return -1;
        }
    }

    public static void addWatchtime(TwitchChannel channel, User user, int minutes) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Watchtimes WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
            if (!rs.next()) {
                SystemAPI.get().database().execute("INSERT INTO Watchtimes (channelId, userId, username, minutes) VALUES ('" + channel.getId() + "', '" + user.getId() + "','" + user.getDisplayName() + "', " + minutes + ");");
            } else {
                int newCount = Integer.parseInt(rs.getString("minutes")) + minutes;
                SystemAPI.get().database().execute("UPDATE Watchtimes SET minutes=" + newCount + ", username='" + user.getDisplayName() + "' WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
            }

            try { rs.close(); } catch (Exception ignored) { }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void removeWatchtime(TwitchChannel channel, User user, int minutes) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Watchtimes WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
            if (!rs.next()) {
                try {
                    rs.close();
                } catch (Exception var6) {
                }

            } else {
                int count = Integer.parseInt(rs.getString("minutes"));
                if (count - minutes <= 0) {
                    SystemAPI.get().database().execute("DELETE FROM Watchtimes WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
                } else {
                    SystemAPI.get().database().execute("UPDATE Watchtimes SET minutes=" + (count - minutes) + ", username='" + user.getDisplayName() + "' WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
                }

                try { rs.close(); } catch (Exception ignored) { }

            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void resetWatchtime(TwitchChannel channel, User user) {
        try {
            SystemAPI.get().database().execute("DELETE FROM Watchtimes WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static long getWatchtime(TwitchChannel channel, User user) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Watchtimes WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                return 0L;
            } else {
                long count = rs.getLong("minutes");
                try { rs.close(); } catch (Exception ignored) { }
                return count;
            }
        } catch (Exception err) {
            err.printStackTrace();
            return -1L;
        }
    }

    public static boolean isBlocked(TwitchChannel channel, User user, String type) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM BlockedUsers WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "' AND type='" + type + "';");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }

                return false;
            } else {
                try { rs.close(); } catch (Exception ignored) { }
                return true;
            }
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public static boolean isBlocked(TwitchChannel channel, String userId, String type) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM BlockedUsers WHERE userId='" + userId + "' AND channelId='" + channel.getId() + "' AND type='" + type + "';");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }

                return false;
            } else {
                try { rs.close(); } catch (Exception ignored) { }
                return true;
            }
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public static void block(TwitchChannel channel, User user, String type) {
        try {
            SystemAPI.get().database().execute("INSERT INTO BlockedUsers (channelId, userId, type) VALUES ('" + channel.getId() + "', '" + user.getId() + "', '" + type + "');");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void unblock(TwitchChannel channel, User user, String type) {
        try {
            SystemAPI.get().database().execute("DELETE FROM BlockedUsers WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "' AND type='" + type + "';");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void addTemporaryStatistic(TwitchChannel channel, User user, String type, int amount) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM TemporaryStatistics WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
            int messages = 0;
            int minutes = 0;
            if (!rs.next()) {
                SystemAPI.get().database().execute("INSERT INTO TemporaryStatistics (channelId, userId, username, messages, minutes, started) VALUES ('" + channel.getId() + "', '" + user.getId() + "', '"+user.getDisplayName()+"', 0, 0, " + (System.currentTimeMillis()/1000) + ");");
            } else {
                messages = rs.getInt("messages");
                minutes = rs.getInt("minutes");
            }
            if (type.equals("messages")) {
                messages += amount;
            } else if (type.equals("watchtime") || type.equals("minutes")) {
                minutes += amount;
            }
            SystemAPI.get().database().execute("UPDATE TemporaryStatistics SET messages=" + messages + ", minutes=" + minutes + ", username='"+user.getDisplayName()+"' WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
            try { rs.close(); } catch (Exception ignored) { }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static HashMap<String, Integer> getTemporaryStatistic(TwitchChannel channel, User user) {
        HashMap<String, Integer> stats = new HashMap<>();
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM TemporaryStatistics WHERE userId='" + user.getId() + "' AND channelId='" + channel.getId() + "';");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                return stats;
            }
            stats.put("messages", rs.getInt("messages"));
            stats.put("minutes", rs.getInt("minutes"));
            try { rs.close(); } catch (Exception ignored) { }
            return stats;
        } catch (Exception err) {
            err.printStackTrace();
            return stats;
        }
    }

}
