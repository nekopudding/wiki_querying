package cpen221.mp3;

import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.List;

public class Tests_Task5 {

    private static int TIMEOUT = 10;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(TIMEOUT);

    @Test
    public void testString() {


        List<String> w = WikiMediator.executeQuery("get category where (author is 'CLCStudent' and (title is 'Barack Obama' or title is 'Naomi Klein'))");



    }




}
