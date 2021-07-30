package cse41321.containers;

import cse41321.containers.Homework5.ChainedHashTable;
import cse41321.containers.Homework5.KeyValuePair;
import cse41321.containers.Homework5.SinglyLinkedList;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

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

    @Test
    public void testTheSinglyLinkedListConstructor() {
        KeyValuePair<Integer, String> keyValuePair = new KeyValuePair<>(548962115, "Edgar Cole");
        SinglyLinkedList<KeyValuePair<Integer, String>> singlyLinkedList = new SinglyLinkedList<>();
        singlyLinkedList.insertHead(keyValuePair);
        assertEquals(singlyLinkedList.getHead().getData().getValue(), "Edgar Cole");
        // Here we go again with the explicit cast. I guess I still have a bit to learn about how generics work.
        assertEquals((int) singlyLinkedList.getHead().getData().getKey(), 548962115);
    }

    @Test
    public void theChainedHashTableConstructorTestBeforeModification() {
        ChainedHashTable<Integer, String> chainedHashTable = new ChainedHashTable<>(11);
        chainedHashTable.insert(548962115, "Edgar Cole");
        assertTrue(chainedHashTable.contains(548962115));
        chainedHashTable.insert(123456789, "Adrienne Davis");
        assertEquals(chainedHashTable.lookup(123456789), "Adrienne Davis");
        assertEquals(chainedHashTable.getSize(), 2);
        chainedHashTable.remove(548962115);
        assertEquals(chainedHashTable.getSize(), 1);
        assertEquals(chainedHashTable.lookup(123456789), "Adrienne Davis");
    }

    @Test
    public void theModifiedConstructorForTheChainedHashTable() {
        ChainedHashTable<Integer, String> chainedHashTable = new ChainedHashTable<>(11, 1.5, 2);
        assertTrue(chainedHashTable.isEmpty());
        chainedHashTable.insert(548962115, "Edgar Cole");
        assertFalse(chainedHashTable.isEmpty());
    }

    @Test
    public void theResizeMethod() {
        ChainedHashTable<Integer, String> chainedHashTable = new ChainedHashTable<>(7, 1.5, 4);
        assertTrue(chainedHashTable.isEmpty());
        chainedHashTable.insert(123456789, "Kevin Cole");
        chainedHashTable.insert(234567890, "David Cole");
        chainedHashTable.insert(345678901, "Kamilah Cole");
        chainedHashTable.insert(456789012, "Edgar Cole");
        assertEquals(chainedHashTable.getSize(), 4);
        assertEquals(chainedHashTable.lookup(345678901), "Kamilah Cole");
        assertEquals(chainedHashTable.getBuckets(), 7);
        chainedHashTable.resizeTable();
        assertEquals(chainedHashTable.getBuckets(), 28);
        assertEquals(chainedHashTable.getSize(), 4);
        assertEquals(chainedHashTable.lookup(345678901), "Kamilah Cole");
        assertEquals(chainedHashTable.lookup(123456789), "Kevin Cole");
    }

    @Test
    public void autoResizing() {
        ChainedHashTable<Integer, String> chainedHashTable = new ChainedHashTable<>(3, 1.5, 4);
        assertTrue(chainedHashTable.isEmpty());
        chainedHashTable.insert(123456789, "Kevin Cole");
        chainedHashTable.insert(234567890, "David Cole");
        chainedHashTable.insert(345678901, "Kamilah Cole");
        assertEquals(chainedHashTable.lookup(345678901), "Kamilah Cole");
        assertEquals(chainedHashTable.getBuckets(), 3);
        chainedHashTable.insert(567890123, "Cainan Cole");
        assertEquals(chainedHashTable.getSize(), 4);
        assertEquals(chainedHashTable.getBuckets(), 3);
        chainedHashTable.insert(678901234, "Adrienne Davis");
        assertEquals(chainedHashTable.getBuckets(), 3);
        chainedHashTable.insert(789012345, "Jahna Houston");
        assertEquals(chainedHashTable.getBuckets(), 12);
        assertEquals(chainedHashTable.lookup(678901234), "Adrienne Davis");
        assertEquals(chainedHashTable.getSize(), 6);
    }
}