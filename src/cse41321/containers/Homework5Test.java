package cse41321.containers;

import cse41321.containers.Homework5.KeyValuePair;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import static org.testng.Assert.assertEquals;

public class Homework5Test {

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void testTheKeyValuePairConstructor() {
        KeyValuePair<Integer, String> keyValuePair = new KeyValuePair<>(548962115, "Edgar Cole");
        // I'm not sure why I expected that in the next statement some sort of auto-unboxing would occur.
        assertEquals((int) keyValuePair.getKey(), 548962115);
        assertEquals(keyValuePair.getValue(), "Edgar Cole");
    }

}