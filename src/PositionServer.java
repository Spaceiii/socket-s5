import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PositionServer {
    static Map<Integer, List<Position>> positions = new HashMap<>();
    static int clientNextId = 1;

    static final String REQ_ERR = "REQ_ERR";
    static final String NOID_ERR = "NOID_ERR";
    static final String CMD_ERR = "CMD_ERR";
    static final String OK = "OK";
    static final String TRUE = "TRUE";
    static final String FALSE = "FALSE";

    public static void main(String[] args) {
        BufferedReader br; // pour lire du texte sur la socket
        PrintStream ps; // pour envoyer du texte sur la socket
        String line; // la ligne reçu/envoyée
        ServerSocket conn = null;
        Socket sock;
        int port = -1;

        if (args.length != 1) {
            System.out.println("usage: Server port");
            System.exit(1);
        }

        try {
            port = Integer.parseInt(args[0]); // récupération du port sous forme int
            conn = new ServerSocket(port); // création socket serveur
        } catch (Exception e) {
            System.out.println("problème création socket serveur : " + e.getMessage());
            System.exit(1);
        }

        while (true) { // boucle infinie pour attendre et traiter les connexions des clients
            try {
                sock = conn.accept(); // attente connexion client
                br = new BufferedReader(new java.io.InputStreamReader(sock.getInputStream())); // creation flux lecture lignes de textes
                ps = new PrintStream(sock.getOutputStream()); // création flux écriture lignes de texte

                ps.println(clientNextId);
                positions.put(clientNextId, new ArrayList<>());

                clientNextId++;

                while ((line = br.readLine()) != null) {
                    String[] req = line.split(" ");
                    if (req.length < 2) {
                        ps.println(REQ_ERR);
                        continue;
                    }
                    int reqId = Integer.parseInt(req[0]);
                    int clientId = Integer.parseInt(req[1]);
                    if (!positions.containsKey(clientId)) {
                        ps.println(NOID_ERR);
                    }
                    String params = req.length > 2 ? req[2] : "";
                    params = req.length > 3 ? params + " " + req[3] : params;

                    System.out.println("Client " + clientId + " sent request " + reqId + " with params: " + params);

                    switch (reqId) {
                        case 1:
                            memorizePosition(clientId, params, ps);
                            break;
                        case 2:
                            calculateDistance(clientId, ps);
                            break;
                        case 3:
                            alreadyPassed(clientId, params, ps);
                            break;
                        default:
                            ps.println(CMD_ERR);
                    }
                }
                br.close();
                ps.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void memorizePosition(int id, String cordLine, PrintStream ps) {
        try {
            Position pos = parseCoordinates(cordLine);
            if (pos == null) {
                ps.println(REQ_ERR);
                return;
            }
            List<Position> posList = positions.get(id);
            posList.add(pos);
            ps.println(OK);
        } catch (Exception _) {
            ps.println(REQ_ERR);
        }
    }

    private static void calculateDistance(int id, PrintStream ps) {
        try {
            List<Position> posList = PositionServer.positions.get(id);
            if (posList.size() < 2) {
                ps.println("0.0");
                return;
            }
            double totalDistance = 0.0;
            for (int i = 1; i < posList.size(); i++) {
                totalDistance += posList.get(i).distanceTo(posList.get(i - 1));
            }
            ps.println(totalDistance);
        } catch (Exception _) {
            ps.println(REQ_ERR);
        }
}

private static void alreadyPassed(int id, String params, PrintStream ps) {
    try {
        String[] parts = params.split(" ");
        if (parts.length != 2) {
            ps.println(REQ_ERR);
            return;
        }
        double proximity = Double.parseDouble(parts[0]);
        String coordLine = parts[1];
        Position currentPosition = parseCoordinates(coordLine);
        if (currentPosition == null) {
            ps.println(REQ_ERR);
            return;
        }
        for (Position pos : positions.get(id)) {
            if (currentPosition.distanceTo(pos) <= proximity) {
                ps.println(TRUE);
                return;
            }
        }
        ps.println(FALSE);
    } catch (Exception _) {
        ps.println(REQ_ERR);
    }
}

private static Position parseCoordinates(String line) {
    String[] parts = line.split(",");
    if (parts.length != 3) {
        return null;
    }
    int x, y, z;
    try {
        x = Integer.parseInt(parts[0].trim());
        y = Integer.parseInt(parts[1].trim());
        z = Integer.parseInt(parts[2].trim());
        return new Position(x, y, z);

    } catch (NumberFormatException _) {
        return null;
    }
}
}
