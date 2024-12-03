import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A double-linked list implementation that allows for efficient insertions and deletions at both ends
 * and in the middle. this can store an element of any type
 */
public class IUDoubleLinkedList<T> implements IndexedUnsortedList<T> {

    /** Reference to the first node in the list */
    private Node<T> head;
    /** Reference to the last node in the list */
    private Node<T> tail;
    /** The number of elements in the list */
    private int size;
    /** Modification count for the iterators */
    private int modCount;

    /**
     * Makes an empty double-linked list.
     */
    public IUDoubleLinkedList() {
        head = tail = null;
        size = 0;
        modCount = 0;
    }

    @Override
    public void addToFront(T element) {
        Node<T> newNode = new Node<T>(element);
        newNode.setNextNode(head);
        if (isEmpty()) {
            tail = newNode;
        } else {
            head.setPreviousNode(newNode); // if head is null and you call .(anything) it crashes
        }
        head = newNode;
        size++;
        modCount++;
    }

    @Override
    public void addToRear(T element) {
        Node<T> newNode = new Node<T>(element);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNextNode(newNode);
            newNode.setPreviousNode(tail);
            tail = newNode;
        }
        size++;
        modCount++;
    }

    @Override
    public void add(T element) {
        addToRear(element);
        //
    }

    @Override
    public void addAfter(T element, T target) {
        Node<T> targetNode = head;
        while (targetNode != null && !targetNode.getElement().equals(target)) {
            targetNode = targetNode.getNextNode();
        }
        if (targetNode == null) {
            throw new NoSuchElementException();
        }
        Node<T> newNode = new Node<T>(element);
        newNode.setNextNode(targetNode.getNextNode()); // attach newNode first
        newNode.setPreviousNode(targetNode);
        targetNode.setNextNode(newNode); // then update list
        if (newNode.getNextNode() != null) { // or tail == targetNode
            newNode.getNextNode().setPreviousNode(newNode);
        } else { // adding a new tail
            tail = newNode;
        }
        modCount++;
        size++;
    }

    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            addToFront(element);
        } else if (index == size()) {
            addToRear(element);
        } else {
            Node<T> newNode = new Node<T>(element);
            Node<T> currentNode = head;
            for (int i = 0; i < index; i++) {
                currentNode = currentNode.getNextNode();
            }
            Node<T> prevNode = currentNode.getPreviousNode();
            prevNode.setNextNode(newNode);
            newNode.setPreviousNode(prevNode);
            newNode.setNextNode(currentNode);
            currentNode.setPreviousNode(newNode);
            size++;
            modCount++;
        }
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T element = head.getElement();
        head = head.getNextNode();
        if (head != null) {
            head.setPreviousNode(null);
        } else {
            tail = null; // List is now empty
        }
        size--;
        modCount++;
        return element;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T element = tail.getElement();
        tail = tail.getPreviousNode();
        if (tail != null) {
            tail.setNextNode(null);
        } else {
            head = null;
        }
        size--;
        modCount++;
        return element;
    }

    @Override
    public T remove(T element) {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        Node<T> currentNode = head;
        while (currentNode != null && !currentNode.getElement().equals(element)) {
            currentNode = currentNode.getNextNode();
        }
        if (currentNode == null) {
            throw new NoSuchElementException();
        }
        // Update links
        if (currentNode == head) {
            head = currentNode.getNextNode();
            if (head != null) {
                head.setPreviousNode(null);
            } else {
                tail = null; // List is now empty
            }
        } else if (currentNode == tail) {
            tail = currentNode.getPreviousNode();
            tail.setNextNode(null);
        } else {
            Node<T> prevNode = currentNode.getPreviousNode();
            Node<T> nextNode = currentNode.getNextNode();
            prevNode.setNextNode(nextNode);
            nextNode.setPreviousNode(prevNode);
        }
        size--;
        modCount++;
        return currentNode.getElement();

    }

    @Override
    public T remove(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }

        Node<T> curNode = head;
        for (int i = 0; i < index; i++) {
            curNode = curNode.getNextNode();
        }
        if (index == 0) {
            head = curNode.getNextNode();
        } else {
            curNode.getPreviousNode().setNextNode(curNode.getNextNode());
        }
        if (curNode == tail) {
            tail = curNode.getPreviousNode();
        } else {
            curNode.getNextNode().setPreviousNode(curNode.getPreviousNode());
        }
        size--;
        modCount++;
        return curNode.getElement();
    }

    @Override
    public void set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> currentNode = head;
        for (int i = 0; i < index; i++) {
            currentNode = currentNode.getNextNode();
        }
        currentNode.setElement(element);
        modCount++;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> currentNode = head;
        for (int i = 0; i < index; i++) {
            currentNode = currentNode.getNextNode();
        }
        return currentNode.getElement();
    }

    @Override
    public int indexOf(T element) {
        Node<T> currentNode = head;
        int currentIndex = 0;
        while (currentNode != null && !currentNode.getElement().equals(element)) {
            currentNode = currentNode.getNextNode();
            currentIndex++;
        }
        if (currentNode == null) {
            currentIndex = -1;
        }
        return currentIndex;
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return head.getElement();
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return tail.getElement();
    }

    @Override
    public boolean contains(T target) {
        return indexOf(target) != -1;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
        // return head == null; you can use either one.
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (T element : this) {
            sb.append(element.toString());
            sb.append(", ");
        }
        if (size() > 0) {
            sb.delete(sb.length() - 2, sb.length()); // remove trailing ", "
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new DLLIterator();
    }

    @Override
    public ListIterator<T> listIterator() {
        return new DLLIterator();
    }

    @Override
    public ListIterator<T> listIterator(int startingIndex) {
        return new DLLIterator(startingIndex);
    }

    /** ListIterator (and bassic Iterator) for IUDoubleLinkedList */
    private class DLLIterator implements ListIterator<T> {
        /** The next node to return in the iteration */
        private Node<T> nextNode;

        /** the index of nextNode */
        private int nextIndex;

        /** the modification count for the iterator to detect concurrent modification  */
        private int iterModCount;

        /** The last node returned by next() or previous(), null if none */
        private Node<T> lastReturnedNode;

        /** initializes iterator at the start of the list */
        public DLLIterator() {
            this(0);

        }

        /**
         * Initialize in front of the given starting index
         * 
         * @param startingIndex index to start in front of
         * @throws IndexOutOfBoundsException if the starting index is out of range (startingIndex < 0 || startingIndex > size)
         */
         
        public DLLIterator(int startingIndex) {
            if (startingIndex < 0 || startingIndex > size) {
                throw new IndexOutOfBoundsException();
            }
            nextNode = head; // Should start from the best end
            for (int i = 0; i < startingIndex; i++) {
                nextNode = nextNode.getNextNode();
            }
            lastReturnedNode = null;
            nextIndex = startingIndex;
            iterModCount = modCount;

        }

        @Override
        public boolean hasNext() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            return nextIndex < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastReturnedNode = nextNode;
            T retVal = nextNode.getElement();
            nextNode = nextNode.getNextNode();
            nextIndex++;
            return retVal;
        }

        @Override
        public boolean hasPrevious() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            return nextIndex > 0;
        }

        @Override
        public T previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            if (nextNode == null) {
                nextNode = tail;
            } else {
                nextNode = nextNode.getPreviousNode();
            }
            lastReturnedNode = nextNode;
            nextIndex--;
            return lastReturnedNode.getElement();
        }

        @Override
        public int nextIndex() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            return nextIndex - 1;
        }

        @Override
        public void remove() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            if (lastReturnedNode == null) {
                throw new IllegalStateException();
            }
            if (lastReturnedNode != head) {
                lastReturnedNode.getPreviousNode().setNextNode(lastReturnedNode.getNextNode());
            } else {
                head = head.getNextNode();
            }
            if (lastReturnedNode != tail) {
                lastReturnedNode.getNextNode().setPreviousNode(lastReturnedNode.getPreviousNode());
            } else {
                tail = tail.getPreviousNode();
            }
            if (lastReturnedNode != nextNode) {// last move was next
                nextIndex--; // fewer nodes to the left that there used to be
            } else { // last move was previous
                nextNode = nextNode.getNextNode();
            }
            lastReturnedNode = null;
            size--;
            modCount++;
            iterModCount++;
        }

        @Override
        public void set(T e) {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            if (lastReturnedNode == null) {
                throw new IllegalStateException();
            }
            lastReturnedNode.setElement(e);
            modCount++;
            iterModCount++;
        }

        @Override
        public void add(T e) {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            Node<T> newNode = new Node<>(e);
        
            if (nextIndex == 0) {
                newNode.setNextNode(head);
                if (head != null) {
                    head.setPreviousNode(newNode);
                }
                head = newNode;
                if (tail == null) {
                    tail = newNode;
                }
            } else if (nextIndex == size) {
                if (tail != null) {
                    tail.setNextNode(newNode);
                    newNode.setPreviousNode(tail);
                } else {
                    head = newNode;
                }
                tail = newNode;
            } else {
                Node<T> prevNode = nextNode.getPreviousNode();
                prevNode.setNextNode(newNode);
                newNode.setPreviousNode(prevNode);
                newNode.setNextNode(nextNode);
                nextNode.setPreviousNode(newNode);
            }
            size++;
            modCount++;
            iterModCount++;
            lastReturnedNode = null;
            nextIndex++;
        }

    }

}
