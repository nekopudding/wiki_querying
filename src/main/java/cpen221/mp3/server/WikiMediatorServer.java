package cpen221.mp3.server;

import com.google.gson.Gson;
import cpen221.mp3.wikimediator.WikiMediator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thread-Safety Argument:
 * Everything other than the WikiMediator instance is confined or immutable.
 * The WikiMediator variable reference only changes in the constructor.
 * The WikiMediator getter methods for the WikiMediator object makes copies of its own maps and lists,
 * meaning immutability.
 * For the methods that actually do change the maps and lists (by adding new requests), they all use
 * thread-safe data types, e.g. ConcurrentHashMap.
 */
public class WikiMediatorServer {

    public static final int WIKI_PORT = 3000;
    final private int maxConc;
    final private ServerSocket serverSocket;
    private int numReqs = 0;
    WikiMediator med;
    final static String outputFilePath = "local";

    /**
     * Start a server at a given port number, with the ability to process
     * upto n requests concurrently.
     *
     * @param port the port number to bind the server to
     * @param n    the number of concurrent requests the server can handle
     */
    public WikiMediatorServer(int port, int n) throws IOException {
        serverSocket = new ServerSocket(port);
        maxConc = n;
        try{
            Map<String, Integer> p = loadPageCount("pageCount");
            Map<Long, String> q = loadQueryTime("queryTime");
            List<Long> r = loadRequestTime("requestTime");
            med = new WikiMediator(p,q,r);
        }
        catch (FileNotFoundException e){
            med = new WikiMediator();
        }
    }

    public WikiMediatorServer(int port, int n, Map<String, Integer> p, Map<Long, String> q, List<Long> r) throws IOException{
        serverSocket = new ServerSocket(port);
        maxConc = n;
        med = new WikiMediator(p,q,r);
    }

    /**
     * Run the server, listening for connections and handling them.
     *
     * @throws IOException if the main server socket is broken
     */
    public void serve() throws IOException {
        while (true) {
            if (numReqs <= maxConc) {
                final Socket socket = serverSocket.accept();
                numReqs++;
                System.out.println(numReqs);
                Thread handler = new Thread(new Runnable() {
                    public void run() {
                        try {
                            try {
                                handle(socket);
                            } finally {
                                socket.close();
                                numReqs--;
                            }
                        } catch (IOException ioe) {
                            System.out.println("IO Exception");
                            ioe.printStackTrace();
                        }
                    }
                });
                handler.start();
            }
        }
    }

    /**
     * Handle one client connection. Returns when client disconnects.
     *
     * @param socket socket where client is connected
     * @throws IOException if connection encounters an error
     */
    synchronized private void handle(Socket socket) throws IOException {
        System.err.println("client connected");

        BufferedReader in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));

        PrintWriter out = new PrintWriter(new OutputStreamWriter(
            socket.getOutputStream()));
        Gson gson = new Gson();
            StringBuilder sb = new StringBuilder();
            for (String line = in.readLine(); line != null; line = in
                .readLine()) {
                if (line.contains("}")){
                    System.err.println("request: " + line);
                    sb.append(line);
                    if (sb.toString().contains("\"type\": \"stop")){
                        WikiRequest wr = gson.fromJson(sb.toString(), WikiRequest.class);
                        WikiReply output = wr.runOperation(med);
                        out.print(gson.toJson(output)+"\n");
                        sb = new StringBuilder();
                        out.flush();
                        shutDown();
                    }
                    else if (sb.toString().contains("timeout")){
                        WikiRequestTimeout wr = gson.fromJson(sb.toString(), WikiRequestTimeout.class);
                        WikiReply output = wr.runOperation(med);
                        out.print(gson.toJson(output)+"\n");
                        sb = new StringBuilder();
                        out.flush();
                    }
                    else {
                        WikiRequest wr = gson.fromJson(sb.toString(), WikiRequest.class);
                        WikiReply output = wr.runOperation(med);
                        out.print(gson.toJson(output)+"\n");
                        sb = new StringBuilder();
                        out.flush();
                    }
                }
                else {
                    System.err.println("request: " + line);
                    sb.append(line);
                }
            }
    }

    /**
     * Runs a shutdown sequence for the server.
     *
     * Effects: Saves current maps and lists that keeps track of requests in txt files in /local.
     */
    synchronized private void shutDown(){
        Map<String, Integer> pageCount = med.getPageCount();
        saveMapToFile(pageCount, "pageCount");
        Map<Long, String> queryTime = med.getQueryTime();
        saveMapToFile(queryTime, "queryTime");
        List<Long> requestTime = med.getRequestTime();
        saveListToFile(requestTime, "requestTime");
        System.exit(0);
    }

    /**
     * Saves a map to a txt file. Filename dictated by filename input.
     * Each line of the txt file contains:
     * Key.toString():Value.toString().
     *
     * @param m Map to be saved. Must not be null or empty.
     * @param filename name of the file to be saved. Does not include .txt
     * @param <T> generic type of key.
     * @param <G> generic type of value.
     */
    synchronized private <T,G> void saveMapToFile(Map<T,G> m, String filename){
        File data = new File(outputFilePath+"/"+filename+".txt");
        BufferedWriter bf = null;
        try{
            bf = new BufferedWriter( new FileWriter(data) );
            for(T key : m.keySet()){
                bf.write( key + ":" + m.get(key) );
                bf.newLine();
            }
            bf.flush();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                bf.close();
            }
            catch(Exception e){
                System.out.println("buffered writer failed to close.");
            }
        }
    }

    /**
     * Saves a list to a txt file in /local.
     * Each line of the txt file represents each element of the list.
     *
     * @param l List to be saved. Must not be null or empty.
     * @param filename name of the file to be saved. Does not include .txt
     * @param <T> generic type of list.
     */
    synchronized private <T> void saveListToFile(List<T> l, String filename){
        File data = new File(outputFilePath+"/"+filename+".txt");
        BufferedWriter bf = null;
        try{
            bf = new BufferedWriter( new FileWriter(data) );
            for(T e : l){
                bf.write( e.toString() );
                bf.newLine();
            }
            bf.flush();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                bf.close();
            }
            catch(Exception e){
                System.out.println("buffered writer failed to close.");
            }
        }
    }

    /** Helper method to import the pageCount map back into the server.
     *
     * @param filename name of page count map text file. Does not contain .txt. Must be a text file
     *                 formatted in the style of:
     *                 Key:Value
     *                 Key2:Value2
     *                 etc.
     * @return Map<String, Integer> taken from the specified filename.
     * @throws FileNotFoundException checked exception if no file found.
     */
    synchronized static Map<String,Integer> loadPageCount(String filename) throws FileNotFoundException {
        Map<String, Integer> output = new HashMap<String, Integer>();
        File file = new File(outputFilePath+"/"+filename+".txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");

                String key = parts[0].trim();
                Integer value = Integer.parseInt(parts[1].trim());
                if (!key.equals("") && !value.equals(""))
                    output.put(key, value);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return output;
    }

    /** Helper method to import the queryTime back into the server.
     *
     * @param filename name of queryTime map text file. Does not contain .txt. Must be a text file
     *      *                 formatted in the style of:
     *      *                 Key:Value
     *      *                 Key2:Value2
     *      *                 etc.
     * @return Map<Long, String> taken from the specified filename.
     * @throws FileNotFoundException if the file is not found.
     */
    synchronized private static Map<Long, String> loadQueryTime(String filename) throws FileNotFoundException {
        Map<Long, String> output = new HashMap<Long, String>();
        File file = new File(outputFilePath+"/"+filename+".txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");

                Long key = Long.parseLong(parts[0].trim());
                String value = parts[1].trim();
                if (!key.equals("") && !value.equals(""))
                    output.put(key, value);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return output;
    }

    /** Helper method to load the requestTime list back into the server.
     *
     * @param filename name of request Time list file. Does not contain .txt. Must be a text file
     *                 formatted so each element is in its own line. Each element must be a long.
     * @return
     * @throws FileNotFoundException
     */
    synchronized private static List<Long> loadRequestTime(String filename) throws FileNotFoundException {
        List<Long> output = new ArrayList<>();
        File file = new File(outputFilePath+"/"+filename+".txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                Long key = Long.parseLong(line.trim());
                if (!key.equals(""))
                    output.add(key);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return output;
    }


    public static void main(String[] args) {
        try {
            try{
                Map<String, Integer> p = loadPageCount("pageCount");
                Map<Long, String> q = loadQueryTime("queryTime");
                List<Long> r = loadRequestTime("requestTime");
                WikiMediatorServer server = new WikiMediatorServer(WIKI_PORT, 8, p, q, r);
                server.serve();
            }
            catch (FileNotFoundException e){
                WikiMediatorServer server = new WikiMediatorServer(WIKI_PORT, 8);
                server.serve();
            }

        } catch (IOException e) {
            System.out.println("IO EXCEPTION");
            e.printStackTrace();
        }
    }

}
