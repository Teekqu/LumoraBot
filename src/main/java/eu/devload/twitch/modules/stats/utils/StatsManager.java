package eu.devload.twitch.modules.stats.utils;

import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.objects.UserObject;
import eu.devload.twitch.utils.SystemAPI;

import java.sql.ResultSet;

public class StatsManager {

    public static void updateHighestSubCount(TwitchChannel ch, int newSubs, boolean check) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM HighestSubCount WHERE channelId='"+ch.id()+"';");
            if (!rs.next()) {
                SystemAPI.get().database().execute("INSERT INTO HighestSubCount (channelId, count, time) VALUES ('" + ch.id() + "', " + newSubs + ", " + (System.currentTimeMillis()/1000) + ");");
            } else {
                int subCount = rs.getInt("count");
                if (!check || newSubs > subCount) {
                    SystemAPI.get().database().execute("UPDATE HighestSubCount SET count=" + newSubs + ", time="+(System.currentTimeMillis()/1000)+" WHERE channelId='" + ch.id() + "';");
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static int getHighestSubCount(TwitchChannel ch) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM HighestSubCount WHERE channelId='"+ch.id()+"';");
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
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Watchtimes WHERE channelId='" + ch.id() + "' AND userId='0';");
            if (!rs.next()) {
                SystemAPI.get().database().execute("INSERT INTO Watchtimes (channelId, userId, username, minutes) VALUES ('" + ch.id() + "', '0', 'watchtime.max.value', '" + watchtime + "');");
            } else {
                long newMinutes = rs.getLong("minutes") + (long)watchtime;
                SystemAPI.get().database().execute("UPDATE Watchtimes SET minutes='" + newMinutes + "' WHERE channelId='" + ch.id() + "' AND userId='0';");
            }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void addMessageCount(TwitchChannel channel, UserObject user, int amount) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM MessageCounts WHERE userId='" + user.id() + "' AND channelId='" + channel.id() + "';");
            if (!rs.next()) {
                SystemAPI.get().database().execute("INSERT INTO MessageCounts (channelId, userId, username, messageCount) VALUES ('" + channel.id() + "', '" + user.id() + "','" + user.displayName() + "', " + amount + ");");
            } else {
                int newCount = Integer.parseInt(rs.getString("messageCount")) + amount;
                SystemAPI.get().database().execute("UPDATE MessageCounts SET messageCount=" + newCount + ", username='" + user.displayName() + "' WHERE userId='" + user.id() + "' AND channelId='" + channel.id() + "';");
            }
            try { rs.close(); } catch (Exception ignored) { }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void removeMessageCount(TwitchChannel channel, UserObject user, int amount) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM MessageCounts WHERE userId='" + user.id() + "' AND channelId='" + channel.id() + "';");
            if (!rs.next()) {
                try {
                    rs.close();
                } catch (Exception var6) {
                }

            } else {
                int count = Integer.parseInt(rs.getString("messageCount"));
                if (count - amount <= 0) {
                    SystemAPI.get().database().execute("DELETE FROM MessageCounts WHERE userId='" + user.id() + "' AND channelId='" + channel.id() + "';");
                } else {
                    SystemAPI.get().database().execute("UPDATE MessageCounts SET messageCount=" + (count - amount) + ", username='" + user.displayName() + "' WHERE userId='" + user.id() + "' AND channelId='" + channel.id() + "';");
                }

                try { rs.close(); } catch (Exception ignored) { }

            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void resetMessageCount(TwitchChannel channel, String userId) {
        try {
            SystemAPI.get().database().execute("DELETE FROM MessageCounts WHERE userId='" + userId + "' AND channelId='" + channel.id() + "';");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static int getMessageCount(TwitchChannel channel, String userId) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM MessageCounts WHERE userId='" + userId + "' AND channelId='" + channel.id() + "';");
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

    public static void addWatchtime(TwitchChannel channel, UserObject user, int minutes) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Watchtimes WHERE userId='" + user.id() + "' AND channelId='" + channel.id() + "';");
            if (!rs.next()) {
                SystemAPI.get().database().execute("INSERT INTO Watchtimes (channelId, userId, username, minutes) VALUES ('" + channel.id() + "', '" + user.id() + "','" + user.displayName() + "', " + minutes + ");");
            } else {
                int newCount = Integer.parseInt(rs.getString("minutes")) + minutes;
                SystemAPI.get().database().execute("UPDATE Watchtimes SET minutes=" + newCount + ", username='" + user.displayName() + "' WHERE userId='" + user.id() + "' AND channelId='" + channel.id() + "';");
            }

            try { rs.close(); } catch (Exception ignored) { }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void removeWatchtime(TwitchChannel channel, UserObject user, int minutes) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Watchtimes WHERE userId='" + user.id() + "' AND channelId='" + channel.id() + "';");
            if (!rs.next()) {
                try {
                    rs.close();
                } catch (Exception var6) {
                }

            } else {
                int count = Integer.parseInt(rs.getString("minutes"));
                if (count - minutes <= 0) {
                    SystemAPI.get().database().execute("DELETE FROM Watchtimes WHERE userId='" + user.id() + "' AND channelId='" + channel.id() + "';");
                } else {
                    SystemAPI.get().database().execute("UPDATE Watchtimes SET minutes=" + (count - minutes) + ", username='" + user.displayName() + "' WHERE userId='" + user.id() + "' AND channelId='" + channel.id() + "';");
                }

                try { rs.close(); } catch (Exception ignored) { }

            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void resetWatchtime(TwitchChannel channel, String userId) {
        try {
            SystemAPI.get().database().execute("DELETE FROM Watchtimes WHERE userId='" + userId + "' AND channelId='" + channel.id() + "';");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static long getWatchtime(TwitchChannel channel, String userId) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Watchtimes WHERE userId='" + userId + "' AND channelId='" + channel.id() + "';");
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

    public static boolean isBlocked(TwitchChannel channel, UserObject user, String type) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM BlockedUsers WHERE userId='" + user.id() + "' AND channelId='" + channel.id() + "' AND type='" + type + "';");
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
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM BlockedUsers WHERE userId='" + userId + "' AND channelId='" + channel.id() + "' AND type='" + type + "';");
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

    public static void block(TwitchChannel channel, String userId, String type) {
        try {
            SystemAPI.get().database().execute("INSERT INTO BlockedUsers (channelId, userId, type) VALUES ('" + channel.id() + "', '" + userId + "', '" + type + "');");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void unblock(TwitchChannel channel, String userId, String type) {
        try {
            SystemAPI.get().database().execute("DELETE FROM BlockedUsers WHERE userId='" + userId + "' AND channelId='" + channel.id() + "' AND type='" + type + "';");
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

}
