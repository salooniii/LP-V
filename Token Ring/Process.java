import java.io.*;
import java.net.*;

public class Process {

    public static void main(String[] args) throws Exception {

        int id = Integer.parseInt(args[0]);
        int myPort = Integer.parseInt(args[1]);
        int nextPort = Integer.parseInt(args[2]);

        ServerSocket serverSocket = new ServerSocket(myPort);
        System.out.println("Process " + id + " listening on port " + myPort);

        // Connect to next process (retry until available)
        Socket next = null;
        while (next == null) {
            try {
                next = new Socket("localhost", nextPort);
            } catch (Exception e) {
                Thread.sleep(1000);
            }
        }

        DataOutputStream out = new DataOutputStream(next.getOutputStream());

        // Accept connection from previous process
        Socket prev = serverSocket.accept();
        DataInputStream in = new DataInputStream(prev.getInputStream());

        // Start token only from Process 1
        if (id == 1) {
            Thread.sleep(2000); // wait for all to connect
            System.out.println("\n🔵 Process 1 STARTS THE TOKEN\n");
            out.writeUTF("TOKEN");
        }

        while (true) {

            String msg = in.readUTF();

            if (msg.equals("TOKEN")) {

                System.out.println("\n====================================");
                System.out.println("🔷 Process " + id + " RECEIVED TOKEN");

                // Delay before entering CS
                Thread.sleep(1000);

                System.out.println("➡ Process " + id + " ENTERING CRITICAL SECTION");
                Thread.sleep(2000); // simulate work

                System.out.println("⬅ Process " + id + " EXITING CRITICAL SECTION");

                // Delay before passing token
                Thread.sleep(2000);

                System.out.println("Process " + id + " PASSING TOKEN to next process");
                System.out.println("====================================\n");

                out.writeUTF("TOKEN");
            }
        }
    }
}





/*
import java.io.*;
import java.net.*;

public class Process {

    public static void main(String[] args) throws Exception {

        int id = Integer.parseInt(args[0]);
        int myPort = Integer.parseInt(args[1]);
        int nextPort = Integer.parseInt(args[2]);

        // Number of token rounds
        int rounds = 3;

        ServerSocket serverSocket = new ServerSocket(myPort);
        System.out.println("Process " + id + " listening on port " + myPort);

        // Connect to next process
        Socket next = null;

        while (next == null) {
            try {
                next = new Socket("localhost", nextPort);
            } catch (Exception e) {
                Thread.sleep(1000);
            }
        }

        DataOutputStream out = new DataOutputStream(next.getOutputStream());

        // Accept previous process connection
        Socket prev = serverSocket.accept();
        DataInputStream in = new DataInputStream(prev.getInputStream());

        // Start token from Process 1
        if (id == 1) {
            Thread.sleep(2000);
            System.out.println("\n🔵 Process 1 STARTS THE TOKEN\n");
            out.writeUTF("TOKEN");
        }

        // Run only limited rounds
        for (int count = 0; count < rounds; count++) {

            String msg = in.readUTF();

            if (msg.equals("TOKEN")) {

                System.out.println("\n====================================");
                System.out.println("🔷 Process " + id + " RECEIVED TOKEN");

                Thread.sleep(1000);

                System.out.println("➡ Process " + id + " ENTERING CRITICAL SECTION");

                Thread.sleep(2000);

                System.out.println("⬅ Process " + id + " EXITING CRITICAL SECTION");

                Thread.sleep(1000);

                System.out.println("Process " + id + " PASSING TOKEN to next process");
                System.out.println("====================================\n");

                out.writeUTF("TOKEN");
            }
        }

        System.out.println("Process " + id + " finished execution.");

        // Close connections
        in.close();
        out.close();
        prev.close();
        next.close();
        serverSocket.close();
    }
}
*/