package cpen221.mp3;

import cpen221.mp3.example.GetThread;
import cpen221.mp3.example.PutThread;
import cpen221.mp3.fsftbuffer.*;
import cpen221.mp3.server.WikiMediatorClient;
import cpen221.mp3.server.WikiMediatorServer;
import org.junit.Test;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;

import static org.junit.Assert.*;
public class Tests_Server {

    @Test
    public void test1() throws IOException {
        WikiMediatorClient client = new WikiMediatorClient("192.168.50.219",
            WikiMediatorServer.WIKI_PORT);
        client.sendRequest("{\n" +
            "\t\"id\": \"1\",\n" +
            "\t\"type\": \"search\",\n" +
            "\t\"query\": \"Dog\",\n" +
            "\t\"limit\": \"4\"\n" +
            "}\n");
        String y = client.getReply();
        assertEquals("{\"id\":\"1\",\"status\":\"success\",\"response\":[\"Dog (disambiguation)\",\"Dog\",\"Difference of Gaussians\",\"Talk:Dog\"]}", y);

        client.sendRequest("{\n" +
            "\t\"id\": \"1\",\n" +
            "\t\"type\": \"search\",\n" +
            "\t\"query\": \"Barack Obama\",\n" +
            "\t\"limit\": \"2\"\n" +
            "}\n");
        String z = client.getReply();
        assertEquals(
            "{\"id\":\"1\",\"status\":\"success\",\"response\":[\"Barack Obama\",\"Barack Obama in comics\"]}", z);

        client.sendRequest("{\n" +
            "   \"id\": \"1\",\n" +
            "   \"type\": \"search\",\n" +
            "   \"query\": \"Euseius_minutisetus\",\n" +
            "   \"timeout\": \"1\"\n" +
            "}\n");
        String a = client.getReply();
        assertEquals("{\"id\":\"1\",\"status\":\"failed\",\"response\":\"Invalid type of operation.\"}", a);

        client.sendRequest("{\n" +
            "   \"id\": \"1\",\n" +
            "   \"type\": \"getPage\",\n" +
            "   \"pageTitle\": \"Euseius_minutisetus\",\n" +
            "   \"timeout\": \"1\"\n" +
            "}\n");
        String b = client.getReply();
        assertEquals("{\"id\":\"1\",\"status\":\"success\",\"response\":\"{{Speciesbox\\n| genus \\u003d Euseius\\n| species \\u003d minutisetus\\n| authority \\u003d Moraes \\u0026 McMurtry, 1989\\n}}\\n\\n\\u0027\\u0027\\u0027\\u0027\\u0027Euseius minutisetus\\u0027\\u0027\\u0027\\u0027\\u0027 is a species of mite in the family [[Phytoseiidae]].\\u003cref name\\u003dgbif/\\u003e\\n\\n\\u003d\\u003dReferences\\u003d\\u003d\\n{{Reflist|refs\\u003d\\n\\u003cref name\\u003dgbif\\u003e\\n{{Cite web| title\\u003d\\u0027\\u0027Euseius minutisetus\\u0027\\u0027\\n| url\\u003dhttps://www.gbif.org/species/2187140\\n| website\\u003dGBIF\\n| accessdate\\u003d2020-01-24\\n}}\\u003c/ref\\u003e\\n}}\\n\\n{{Taxonbar|from\\u003dQ6477794}}\\n\\n[[Category:Arachnids]]\\n[[Category:Articles created by Qbugbot]]\\n[[Category:Animals described in 1989]]\\n\\n\\n{{phytoseiidae-stub}}\"}", b);

        client.sendRequest("{\n" +
            "   \"id\": \"1\",\n" +
            "   \"type\": \"getPage\",\n" +
            "   \"pageTitle\": \"Euseius_minutisetus\",\n" +
            "   \"timeout\": \"0\"\n" +
            "}\n");
        String timeout = client.getReply();
        assertEquals("{\"id\":\"1\",\"status\":\"failed\",\"response\":\"Operation timed out\"}", timeout);

        //stop
        client.sendRequest("{\n" +
            "\t\"id\": \"ten\",\n" +
            "\t\"type\": \"stop\"\n" +
            "}\n");
        a = client.getReply();
        System.out.println(a);
        client.close();
    }

    @Test
    public void testZeitgeist() throws IOException {
        WikiMediatorClient client = new WikiMediatorClient("192.168.50.219",
            WikiMediatorServer.WIKI_PORT);
        client.sendRequest("{\n" +
            "\t\"id\": \"1\",\n" +
            "\t\"type\": \"search\",\n" +
            "\t\"query\": \"Dog\",\n" +
            "\t\"limit\": \"1\"\n" +
            "}\n");
        String y = client.getReply();
        client.sendRequest("{\n" +
            "\t\"id\": \"1\",\n" +
            "\t\"type\": \"search\",\n" +
            "\t\"query\": \"Dog\",\n" +
            "\t\"limit\": \"1\"\n" +
            "}\n");
        y = client.getReply();
        client.sendRequest("{\n" +
            "\t\"id\": \"1\",\n" +
            "\t\"type\": \"search\",\n" +
            "\t\"query\": \"Dog\",\n" +
            "\t\"limit\": \"1\"\n" +
            "}\n");
        y = client.getReply();
        client.sendRequest("{\n" +
            "\t\"id\": \"1\",\n" +
            "\t\"type\": \"search\",\n" +
            "\t\"query\": \"Dog\",\n" +
            "\t\"limit\": \"1\"\n" +
            "}\n");
        y = client.getReply();
        client.sendRequest("{\n" +
            "\t\"id\": \"two\",\n" +
            "\t\"type\": \"zeitgeist\",\n" +
            "\t\"limit\": \"1\"\n" +
            "}\n");
        String a = client.getReply();
        assertEquals("{\"id\":\"two\",\"status\":\"success\",\"response\":[\"Dog\"]}", a);

        client.sendRequest("{\n" +
            "\t\"id\": \"two\",\n" +
            "\t\"type\": \"trending\",\n" +
            "\t\"limit\": \"1\"\n" +
            "}\n");
        String b = client.getReply();
        assertEquals("{\"id\":\"two\",\"status\":\"success\",\"response\":[\"Dog\"]}", b);

        client.sendRequest("{\n" +
            "\t\"id\": \"two\",\n" +
            "\t\"type\": \"peakLoad30s\"\n" +
            "}\n");
        String c = client.getReply();
        assertEquals("{\"id\":\"two\",\"status\":\"success\",\"response\":9}", c);

        client.sendRequest("{\n" +
            "\t\"id\": \"ten\",\n" +
            "\t\"type\": \"stop\"\n" +
            "}\n");
        a = client.getReply();
        System.out.println(a);
        client.close();
    }
}
