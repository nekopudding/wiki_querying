package cpen221.mp3;

import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Test;

import java.util.List;

public class Tests_Task5 {

    @Test
    public void testString() {

        List<String> w = WikiMediator.executeQuery("get category where (author is 'CLCStudent' and (title is 'Barack Obama' or title is 'Naomi Klein'))");

    }




}
