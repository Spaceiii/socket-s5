import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class PositionClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("usage: PositionClient ip_server port");
            System.exit(1);
        }

        String host;
        BufferedReader br; // pour lire du texte sur la socket
        PrintStream ps; // pour écrire du texte sur la socket
        String line;
        Socket sock;
        Scanner sc = new Scanner(System.in);

        host = args[0];
        int port = -1;

        try {
            port = Integer.parseInt(args[1]);
            sock = new Socket(host, port); // création socket client et connexion au serveur donné en args[0]
        } catch (UnknownHostException e) {
            System.out.println("hôte inconnu : " + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("problème de connexion au serveur : " + e.getMessage());
            return;
        }

        try {
            br = new BufferedReader(new java.io.InputStreamReader(sock.getInputStream())); // création flux lecture lignes de texte
            ps = new PrintStream(sock.getOutputStream()); // création flux écriture lignes de texte
        } catch (IOException e) {
            System.out.println("problème création flux sur la socket : " + e.getMessage());
            return;
        }

        String message;
        System.out.println("Bienvenue dans le client PositionClient.");
        System.out.println("Veuillez entrer vos commandes.");
        System.out.println("-----------------------------------------------------");
        System.out.println("[1] Pour envoyer une position : storepos <x>,<y>,<z>");
        System.out.println("[2] Pour calculer la distance entre vos points : pathlen");
        System.out.println("[3] Pour voir si une position est dans votre parcours : findpos <proximity> <x>,<y>,<z>");
        System.out.println("[4] Pour quitter : exit");
        System.out.println();
        System.out.println("Note : Les coordonnées ainsi que la proximité doivent être des nombres à virgule flottante.");
        System.out.println("-----------------------------------------------------");

        int clientId = -1;
        try {
            clientId = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            System.out.println("problème de lecture de l'ID client : " + e.getMessage());
            return;
        }

        System.out.println("Votre ID client est : " + clientId);

        while ((message = sc.nextLine()) != null) {
            if (message.equalsIgnoreCase("exit") || message.equalsIgnoreCase("quit") || message.trim().isEmpty()) {
                System.out.println("Fermeture de la connexion.");
                try {
                    br.close();
                    ps.close();
                    sock.close();
                } catch (IOException e) {
                    System.out.println("problème fermeture flux : " + e.getMessage());
                }
                return;
            }

            String[] parts = message.split(" ", 2);
            if (parts.length < 1) {
                System.out.println("Commande invalide. Veuillez réessayer.");
                continue;
            }

            String command = parts[0];
            String params = parts.length > 1 ? parts[1] : "";

            String request;
            switch (command.toLowerCase()) {
                case "storepos":
                    request = "1 " + clientId + " " + params;
                    break;
                case "pathlen":
                    request = "2 " + clientId;
                    break;
                case "findpos":
                    request = "3 " + clientId + " " + params;
                    break;
                default:
                    System.out.println("Commande inconnue. Veuillez réessayer.");
                    continue;
            }

            try {
                ps.println(request); // envoi du texte donné au serveur
                line = br.readLine(); // lecture réponse serveur
                if (line == null) {
                    System.out.println("Le serveur a fermé la connexion.");
                    break;
                }
                switch (line) {
                    case "REQ_ERR":
                        System.out.println("Le serveur a rencontré une erreur de requête, veuillez vérifier votre saisie.");
                        break;
                    case "NOID_ERR":
                        System.out.println("L'ID client n'existe pas sur le serveur.");
                        break;
                    case "CMD_ERR":
                        System.out.println("Le serveur n'a pas reconnu la commande.");
                        break;
                    default:
                        System.out.println("Réponse du serveur : " + line);
                }
            } catch (IOException e) {
                System.out.println("problème de communication avec le serveur : " + e.getMessage());
                break;
            }
        }

        try {
            br.close();
            ps.close();
            sock.close();
        } catch (IOException e) {
            System.out.println("problème fermeture flux : " + e.getMessage());
        }
    }
}
