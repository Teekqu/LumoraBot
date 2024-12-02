package eu.devload.twitch.api.admin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.io.IOException;
import java.sql.ResultSet;

public class JoinChannelHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if(exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            String auth = exchange.getRequestHeaders().getFirst("Authorization");
            if(auth == null || !auth.equals("DevLoad q057ht9&Q§w7tq597hgq73%/hrseg798h")) {
                System.out.println("[API] Unauthorized request");
                exchange.sendResponseHeaders(401, 0);
                exchange.getResponseBody().close();
                return;
            }

            String channel = exchange.getRequestURI().getQuery().split("=")[1];

            try {
                ResultSet rs = SystemAPI.get().database().query("SELECT * FROM OauthTokens WHERE id='" + channel + "'");
                if (!rs.next()) {
                    try { rs.close(); } catch (Exception ignored) { }
                    System.out.println("[API] Channel not authenticated");
                    exchange.sendResponseHeaders(404, 0);
                    exchange.getResponseBody().close();
                    return;
                }
                try { rs.close(); } catch (Exception ignored) { }
                boolean success = SystemAPI.get().twitchManager().addChannel(new TwitchChannel(channel));
                if(!success) {
                    System.out.println("[API] Error while trying to join channel");
                    exchange.sendResponseHeaders(500, 0);
                    exchange.getResponseBody().close();
                    return;
                }
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
            } catch (Exception err) {
                err.printStackTrace();
                System.out.println("[API] Error while trying to join channel");
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }

        }

    }
}
