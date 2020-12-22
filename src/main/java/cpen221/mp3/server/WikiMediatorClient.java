package cpen221.mp3.server;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;

/**
 * FibonacciClient is a client that sends requests to the FibonacciServer
 * and interprets its replies.
 * A new FibonacciClient is "open" until the close() method is called,
 * at which point it is "closed" and may not be used further.
 */
public class WikiMediatorClient {
    private static final int N = 100;
    private Socket socket;
    private BufferedReader in;
    // Rep invariant: socket, in, out != null
    private PrintWriter out;

    /**
     * Make a FibonacciClient and connect it to a server running on
     * hostname at the specified port.
     *
     * @throws IOException if can't connect
     */
    public WikiMediatorClient(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Use a FibonacciServer to find the first N Fibonacci numbers.
     */
    public static void main(String[] args) {
        try {
            WikiMediatorClient client = new WikiMediatorClient("192.168.50.219",
                WikiMediatorServer.WIKI_PORT);
            ArrayList<String> inputs = new ArrayList<>();
            client.sendRequest("{\n" +
                "\t\"id\": \"1\",\n" +
                "\t\"type\": \"search\",\n" +
                "\t\"query\": \"Dog\",\n" +
                "\t\"limit\": \"4\"\n" +
                "}\n");
            String y = client.getReply();
            System.out.println(y);

            client.sendRequest("{\n" +
                    "\t\"id\": \"1\",\n" +
                    "\t\"type\": \"search\",\n" +
                    "\t\"query\": \"Dune\",\n" +
                    "\t\"limit\": \"12\"\n" +
                    "}\n");
            String z = client.getReply();
            System.out.println(z);

            client.sendRequest("{\n" +
                "   \"id\": \"1\",\n" +
                "   \"type\": \"search\",\n" +
                "   \"query\": \"Lionel Messi\",\n" +
                "   \"limit\": \"12\",\n" +
                "   \"timeout\": \"1\"\n" +
                "}\n");
            String a = client.getReply();
            System.out.println(a);

            client.sendRequest("{\n" +
                "\t\"id\": \"two\",\n" +
                "\t\"type\": \"zeitgeist\",\n" +
                "\t\"limit\": \"10\"\n" +
                "}\n");
            a = client.getReply();
            System.out.println(a);

            //stop
            client.sendRequest("{\n" +
                "\t\"id\": \"ten\",\n" +
                "\t\"type\": \"stop\"\n" +
                "}\n");
            a = client.getReply();
            System.out.println(a);
            client.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Send a request to the server. Requires this is "open".
     *
     * @param s to find Fibonacci(x)
     * @throws IOException if network or server failure
     */
    public void sendRequest(String s) throws IOException {
        out.print(s);
        out.flush(); // important! make sure x actually gets sent
    }

    /**
     * Get a reply from the next request that was submitted.
     * Requires this is "open".
     *
     * @return the requested Fibonacci number
     * @throws IOException if network or server failure
     */
    public String getReply() throws IOException {
        String reply = in.readLine();
        if (reply == null) {
            throw new IOException("connection terminated unexpectedly");
        }

        try {
            return reply;
        }
        catch (NumberFormatException nfe) {
            throw new IOException("misformatted reply: " + reply);
        }
    }

    /**
     * Closes the client's connection to the server.
     * This client is now "closed". Requires this is "open".
     *
     * @throws IOException if close fails
     */
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
