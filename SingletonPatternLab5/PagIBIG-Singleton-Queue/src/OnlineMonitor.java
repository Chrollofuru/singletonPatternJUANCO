import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/** Browser monitor. Refreshes queue data every second without reloading. */
public class OnlineMonitor {
    private final QueueManager queue = QueueManager.getInstance();
    private HttpServer server;

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/status", exchange -> {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }
            respond(exchange, "application/json; charset=UTF-8", queue.getStatusJson());
        });
        server.createContext("/", exchange -> {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }
            respond(exchange, "text/html; charset=UTF-8", """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="utf-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1">
                      <title>Pag-IBIG Queue Monitor</title>
                      <style>
                        body { font-family: Arial, sans-serif; background: #eef4f9; text-align: center;
                               padding: 60px 16px; color: #24364b; }
                        main { background: white; max-width: 450px; padding: 30px;
                               margin: auto; border-radius: 14px; box-shadow: 0 5px 24px #0001; }
                        #serving { font-size: 80px; font-weight: bold; color: #1765b4; }
                        p { font-size: 18px; }
                      </style>
                    </head>
                    <body>
                      <main>
                        <h1>Pag-IBIG Queue Monitor</h1>
                        <p>Currently serving</p>
                        <div id="serving">--</div>
                        <p>Last issued ticket: <strong id="issued">--</strong></p>
                        <p>Visitors waiting: <strong id="waiting">--</strong></p>
                        <small id="connection">Connecting...</small>
                      </main>
                      <script>
                        async function refresh() {
                          try {
                            const response = await fetch('/status', {cache: 'no-store'});
                            if (!response.ok) throw new Error('Status unavailable');
                            const data = await response.json();
                            document.getElementById('serving').textContent = data.currentServing;
                            document.getElementById('issued').textContent = data.lastIssued;
                            document.getElementById('waiting').textContent = data.waiting;
                            document.getElementById('connection').textContent = 'Live - updates every second';
                          } catch (error) {
                            document.getElementById('connection').textContent = 'Connection lost';
                          }
                        }
                        refresh();
                        setInterval(refresh, 1000);
                      </script>
                    </body>
                    </html>
                    """);
        });
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private static void respond(HttpExchange exchange, String contentType, String body)
            throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(200, data.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(data);
        }
    }
}
