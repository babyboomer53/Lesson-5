package cse41321.containers;

import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Homework5 {

    /**
     * This class is intended for use with a hash table.
     *
     * @param <K>
     * @param <V>
     */
    static class KeyValuePair<K, V> {
        private K key;
        private V value;

        public KeyValuePair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    /**
     * This class implements a singly-linked list which will be used by the ChainedHashTable class.
     *
     * @param <E>
     */
    static class SinglyLinkedList<E> {
        // An element in a linked list
        class Element {
            private E data;
            private Element next;

            // Only allow SinglyLinkedList to construct Elements
            private Element(E data) {
                this.data = data;
                this.next = null;
            }

            public E getData() {
                return data;
            }

            public Element getNext() {
                return next;
            }

            private SinglyLinkedList getOwner() {
                return SinglyLinkedList.this;
            }
        }

        private Element head;
        private Element tail;
        private int size;

        public Element getHead() {
            return head;
        }

        public Element getTail() {
            return tail;
        }

        public int getSize() {
            return size;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public Element insertHead(E data) {
            Element newElement = new Element(data);

            if (isEmpty()) {
                // Insert into empty list
                head = newElement;
                tail = newElement;
            } else {
                // Insert into non-empty list
                newElement.next = head;
                head = newElement;
            }

            ++size;

            return newElement;
        }

        public Element insertTail(E data) {
            Element newElement = new Element(data);

            if (isEmpty()) {
                // Insert into empty list
                head = newElement;
                tail = newElement;
            } else {
                // Insert into non-empty list
                tail.next = newElement;
                tail = newElement;
            }

            ++size;

            return newElement;
        }

        public Element insertAfter(Element element, E data)
                throws IllegalArgumentException {
            // Check pre-conditions
            if (element == null) {
                throw new IllegalArgumentException(
                        "Argument 'element' must not be null");
            }
            if (element.getOwner() != this) {
                throw new IllegalArgumentException(
                        "Argument 'element' does not belong to this list");
            }

            // Insert new element
            Element newElement = new Element(data);
            if (tail == element) {
                // Insert new tail
                element.next = newElement;
                tail = newElement;
            } else {
                // Insert into middle of list
                newElement.next = element.next;
                element.next = newElement;
            }

            ++size;

            return newElement;
        }

        public E removeHead() throws NoSuchElementException {
            // Check pre-conditions
            if (isEmpty()) {
                throw new NoSuchElementException("Cannot remove from empty list");
            }

            // Remove the head
            Element oldHead = head;
            if (size == 1) {
                // Handle removal of the last element
                head = null;
                tail = null;
            } else {
                head = head.next;
            }

            --size;

            return oldHead.data;
        }

        // Note that there is no removeTail.  This cannot be implemented
        // efficiently because it would require O(n) to scan from head until
        // reaching the item _before_ tail.

        public E removeAfter(Element element)
                throws IllegalArgumentException, NoSuchElementException {
            // Check pre-conditions
            if (element == null) {
                throw new IllegalArgumentException(
                        "Argument 'element' must not be null");
            }
            if (element.getOwner() != this) {
                throw new IllegalArgumentException(
                        "Argument 'element' does not belong to this list");
            }
            if (element == tail) {
                throw new IllegalArgumentException(
                        "Argument 'element' must have a non-null next element");
            }

            // Remove element
            Element elementToRemove = element.next;
            if (elementToRemove == tail) {
                // Remove the tail
                element.next = null;
                tail = element;
            } else {
                // Remove from middle of list
                element.next = elementToRemove.next;
            }

            --size;

            return elementToRemove.data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SinglyLinkedList<?> that = (SinglyLinkedList<?>) o;

            if (this.size != that.size) return false;

            // Return whether all elements are the same
            SinglyLinkedList<?>.Element thisElem = this.getHead();
            SinglyLinkedList<?>.Element thatElem = that.getHead();
            while (thisElem != null && thatElem != null) {
                if (!thisElem.getData().equals(thatElem.getData())) {
                    return false;
                }
                thisElem = thisElem.getNext();
                thatElem = thatElem.getNext();
            }

            return true;
        }
    }

    /**
     * This class implements a chained hash table backed by a singly-linked list.
     *
     * @param <K>
     * @param <V>
     */
    static class ChainedHashTable<K, V> {
        // Table of buckets
        private SinglyLinkedList<KeyValuePair<K, V>>[] table;

        private int size;

        private double maxLoadFactor = 1.5;

        private int resizeMultiplier = 2;

        public ChainedHashTable() {
            this(997);  // A prime number of buckets
        }

        //@SuppressWarnings("unchecked")
        public ChainedHashTable(int buckets) {
            // Create table of empty buckets
            table = new SinglyLinkedList[buckets];
            for (int i = 0; i < table.length; ++i) {
                table[i] = new SinglyLinkedList<KeyValuePair<K, V>>();
            }
            size = 0;
        }

        public ChainedHashTable(int buckets, double maxLoadFactor, int resizeMultiplier) {
            this(buckets);
            this.maxLoadFactor = maxLoadFactor;
            this.resizeMultiplier = resizeMultiplier;
        }

        public int getBuckets() {
            return table.length;
        }

        public int getSize() {
            return size;
        }

        public boolean isEmpty() {
            return getSize() == 0;
        }

        public void insert(K key, V value) throws
                IllegalArgumentException,
                DuplicateKeyException {
            if (key == null) {
                throw new IllegalArgumentException("key must not be null");
            }
            if (contains(key)) {
                throw new DuplicateKeyException();
            }

            // Has the load factor exceeded the threshold?
            if (((double) getSize() / table.length) > maxLoadFactor) resizeTable();

            getBucket(key).insertHead(new KeyValuePair<K, V>(key, value));
            ++size;
        }

        public V remove(K key) throws
                IllegalArgumentException,
                NoSuchElementException {
            if (key == null) {
                throw new IllegalArgumentException("key must not be null");
            }

            // If empty bucket
            SinglyLinkedList<KeyValuePair<K, V>> bucket = getBucket(key);
            if (bucket.isEmpty()) {
                throw new NoSuchElementException();
            }

            // If at head of bucket
            SinglyLinkedList<KeyValuePair<K, V>>.Element elem = bucket.getHead();
            if (key.equals(elem.getData().getKey())) {
                --size;
                return bucket.removeHead().getValue();
            }

            // Scan rest of bucket
            SinglyLinkedList<KeyValuePair<K, V>>.Element prev = elem;
            elem = elem.getNext();
            while (elem != null) {
                if (key.equals(elem.getData().getKey())) {
                    --size;
                    return bucket.removeAfter(prev).getValue();
                }
                prev = elem;
                elem = elem.getNext();
            }

            throw new NoSuchElementException();
        }

        public V lookup(K key) throws
                IllegalArgumentException,
                NoSuchElementException {
            if (key == null) {
                throw new IllegalArgumentException("key must not be null");
            }

            // Scan bucket for key
            SinglyLinkedList<KeyValuePair<K, V>>.Element elem =
                    getBucket(key).getHead();
            while (elem != null) {
                if (key.equals(elem.getData().getKey())) {
                    return elem.getData().getValue();
                }
                elem = elem.getNext();
            }

            throw new NoSuchElementException();
        }

        public boolean contains(K key) {
            try {
                lookup(key);
            } catch (IllegalArgumentException illegalArgumentException) {
                return false;
            } catch (NoSuchElementException noSuchElementException) {
                return false;
            }

            return true;
        }

        private SinglyLinkedList<KeyValuePair<K, V>> getBucket(K key) {
            // Division method
            // return table[Math.abs(key.hashCode()) % table.length];
            // Multiplication method
            return table[(int) (table.length * (0.61 * (int) key % 1))];
        }

        private class KeysIterator implements Iterator<K> {
            private int remaining;  // Number of keys remaining to iterate
            private int bucket;     // Bucket we're iterating
            private SinglyLinkedList<KeyValuePair<K, V>>.Element elem;
            // Position in bucket we're iterating

            public KeysIterator() {
                remaining = ChainedHashTable.this.size;
                bucket = 0;
                elem = ChainedHashTable.this.table[bucket].getHead();
            }

            public boolean hasNext() {
                return remaining > 0;
            }

            public K next() {
                if (hasNext()) {
                    // If we've hit end of bucket, move to next non-empty bucket
                    while (elem == null) {
                        elem = ChainedHashTable.this.table[++bucket].getHead();
                    }

                    // Get key
                    K key = elem.getData().getKey();

                    // Move to next element and decrement entries remaining
                    elem = elem.getNext();
                    --remaining;

                    return key;
                } else {
                    throw new NoSuchElementException();
                }
            }
        }

        public void resizeTable() {
            SinglyLinkedList<KeyValuePair<K, V>>[] newTable = new SinglyLinkedList[table.length * resizeMultiplier];
            SinglyLinkedList<KeyValuePair<K, V>>[] originalTable = table;
            for (int index = 0; index < newTable.length; index++) {
                newTable[index] = new SinglyLinkedList<>();
            }
            this.table = newTable;
            SinglyLinkedList<KeyValuePair<K, V>>.Element element;
            for (int index = 0; index < originalTable.length; index++) {
                element = originalTable[index].head;
                while (element != null) {
                    K key = element.getData().getKey();
                    V value = element.getData().getValue();
                    //table[Math.abs(key.hashCode()) % table.length].insertHead(new KeyValuePair<>(key, value));
                    table[(int) (table.length * (0.61 * (int) key % 1))].insertHead(new KeyValuePair<>(key, value));
                    element = element.getNext();
                }
            }
        }

        @Override
        public String toString() {
            return String.format("Buckets %d; Elements %d; Load factor %.1f; Maximum load factor %.1f; Multiplier %d",
                    table.length,
                    getSize(),
                    (double) getSize() / table.length,
                    maxLoadFactor,
                    resizeMultiplier);
        }

        public Iterable<K> keys() {
            return new Iterable<K>() {
                public Iterator<K> iterator() {
                    return new KeysIterator();
                }
            };
        }
    }

    /**
     * Extends RuntimeException instead of Exception since that's the convention set by NoSuchElementException.
     */
    static class DuplicateKeyException extends RuntimeException {
        public DuplicateKeyException() {
        }

        public DuplicateKeyException(String message) {
            super(message);
        }

        public DuplicateKeyException(Throwable cause) {
            super(cause);
        }

        public DuplicateKeyException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class Driver {
        public static void main(String[] args) {
            ChainedHashTable<Integer, String> chainedHashTable = new ChainedHashTable<>(3, 1.5, 2);
            System.out.println(chainedHashTable);
            System.out.println("Adding Kevin Cole, David Cole and Kamilah Cole…");
            chainedHashTable.insert(123456789, "Kevin Cole");
            chainedHashTable.insert(234567890, "David Cole");
            chainedHashTable.insert(345678901, "Kamilah Cole");
            System.out.println(chainedHashTable);
            System.out.println("Adding Cainan Cole, Adrienne Davis and Jahna Houston…");
            chainedHashTable.insert(567890123, "Cainan Cole");
            chainedHashTable.insert(678901234, "Adrienne Davis");
            chainedHashTable.insert(789012345, "Jahna Houston");
            System.out.println(chainedHashTable);
            System.out.println("Removing David Cole and Adrienne Davis…");
            chainedHashTable.remove(234567890);
            chainedHashTable.remove(678901234);
            System.out.println(chainedHashTable);
        }
    }

}