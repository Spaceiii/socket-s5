import java.io.*;
import java.net.*;

public class SommeServer  {

    public static void main(String []args) {

        BufferedReader br = null; // pour lire du texte sur la socket
        PrintStream ps = null; // pour envoyer du texte sur la socket
        String line = null; // la ligne reçu/envoyée
        ServerSocket conn = null;
        Socket sock = null;
        int port = -1;

        if (args.length != 1) {
            System.out.println("usage: Server port");
            System.exit(1);
        }

        try {
            port = Integer.parseInt(args[0]); // récupération du port sous forme int
            conn = new ServerSocket(port); // création socket serveur
        }
        catch(IOException e) {
            System.out.println("problème création socket serveur : "+e.getMessage());
            System.exit(1);
        }

        while (true) { // boucle infinie pour attendre et traiter les connexions des clients
            try {
                sock = conn.accept(); // attente connexion client
                br = new BufferedReader(new InputStreamReader(sock.getInputStream())); // creation flux lecture lignes de textes
                ps = new PrintStream(sock.getOutputStream()); // création flux écriture lignes de texte
                line = br.readLine();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                continue;
            }

            while (line != null) {
                System.out.println("le client me dit : " + line); // affichage debug
                String[] numbers = line.split(",");
                int sum = 0;
                try {
                    sum = getSum(numbers);
                    ps.println(sum); // envoi de la somme
                }
                catch (NumberFormatException e) {
                    ps.println("REQ_ERR"); // envoi code erreur
                }

                try {
                    line = br.readLine();
                }
                catch (IOException e) {
                    System.out.println(e.getMessage());
                    break;
                }
            }

            try {
                br.close();
                ps.close();
            }
            catch(IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static int getSum(String[] numbers) throws NumberFormatException {
        int sum = 0;
        for (String number : numbers) {
            sum += Integer.parseInt(number.trim());
        }
        return sum;
    }
}