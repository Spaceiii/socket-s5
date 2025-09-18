import java.io.*;
import java.net.*;
import java.util.Scanner;

public class EchoClient  {
    public static void main(String []args) {

        BufferedReader br = null; // pour lire du texte sur la socket
        PrintStream ps = null; // pour écrire du texte sur la socket
        String line = null;
        Socket sock = null;
        Scanner sc = new Scanner(System.in);
        int port = -1;

        if (args.length != 2) {
            System.out.println("usage: EchoClient ip_server port");
            System.exit(1);
        }

        try {
            port = Integer.parseInt(args[1]); // récupération du port sous forme int
            sock = new Socket(args[0],port); // création socket client et connexion au serveur donné en args[0]
        }
        catch(IOException e) {
            System.out.println("problème de connexion au serveur : "+e.getMessage());
            System.exit(1);
        }

        String message;

        try {
            br = new BufferedReader(new InputStreamReader(sock.getInputStream())); // création flux lecture lignes de texte
            ps = new PrintStream(sock.getOutputStream()); // création flux écriture lignes de texte
        } catch (IOException e) {
            System.out.println("problème création flux sur la socket : "+e.getMessage());
            System.exit(1);
        }

        // check for empty line or Ctrl+D to quit

        while (!(message = sc.nextLine()).isEmpty()) {
            try {
                ps.println(message); // envoi du texte donné au serveur
                line = br.readLine(); // lecture réponse serveur
                System.out.println("le serveur me repond : "+line); // affichage debug
            }
            catch(IOException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }

        try {
            br.close();
            ps.close();

            System.out.println("Fermeture de la connexion");

            sock.close();
        } catch (IOException e) {
            System.out.println("problème fermeture flux : "+e.getMessage());
        }
    }
}