import java.io.*;
import java.net.*;

public class BullyProcess {

    int id;
    int port;
    int[] ports;
    boolean isLeader = false;
    boolean receivedOK = false;

    public BullyProcess(int id, int port, int[] ports) {
        this.id = id;
        this.port = port;
        this.ports = ports;
    }

    public void start() throws Exception {

        ServerSocket server = new ServerSocket(port);
        System.out.println("Process " + id + " started on port " + port);

        // Listener Thread
        new Thread(() -> {
            while (true) {
                try {
                    Socket s = server.accept();

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(s.getInputStream()));

                    String msg = in.readLine();

                    // -------- ELECTION MESSAGE --------
                    if (msg.equals("ELECTION")) {

                        System.out.println("Process " + id + " received ELECTION");

                        // Send OK response
                        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

                        out.println("OK");

                        // Start own election
                        new Thread(() -> startElection()).start();
                    }

                    // -------- OK MESSAGE --------
                    else if (msg.equals("OK")) {

                        System.out.println("Process " + id + " received OK");

                        receivedOK = true;
                    }

                    // -------- COORDINATOR MESSAGE --------
                    else if (msg.startsWith("COORDINATOR")) {

                        int leader = Integer.parseInt(msg.split(":")[1]);

                        System.out.println(
                                "Process " + id +
                                        " accepts leader: " + leader);

                        isLeader = false;
                    }

                    s.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // ---------------- ELECTION ----------------
    public void startElection() {

        try {

            System.out.println("Process " + id + " starts election");

            receivedOK = false;

            // Send ELECTION to higher ID processes
            for (int i = id; i < ports.length; i++) {

                try {
                    sendMessage(ports[i], "ELECTION");
                } catch (Exception e) {

                    // Ignore if process not active
                }
            }

            // Wait for OK
            Thread.sleep(2000);

            // If no higher process replied
            if (!receivedOK) {

                becomeLeader();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- LEADER ----------------
    public void becomeLeader() {

        isLeader = true;

        System.out.println(
                "\nProcess " + id + " becomes LEADER\n");

        // Inform all processes
        for (int i = 0; i < ports.length; i++) {

            if (i != (id - 1)) {

                try {

                    sendMessage(
                            ports[i],
                            "COORDINATOR:" + id);

                } catch (Exception e) {
                }
            }
        }
    }

    // ---------------- SEND MESSAGE ----------------
    public void sendMessage(int port, String msg)
            throws Exception {

        Socket s = new Socket("localhost", port);

        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

        out.println(msg);

        s.close();
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args)
            throws Exception {

        int id = Integer.parseInt(args[0]);

        int[] ports = { 5001, 5002, 5003, 5004, 5005 };

        // id-1 because array index starts from 0
        BullyProcess p = new BullyProcess(id, ports[id - 1], ports);

        p.start();

        // Wait before election
        Thread.sleep(3000);

        p.startElection();
    }
}