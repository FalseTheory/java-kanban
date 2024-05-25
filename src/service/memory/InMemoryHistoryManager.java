package service.memory;

import model.Task;
import service.HistoryManager;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Long, Node> links;


    private Node head;
    private Node tail;


    public InMemoryHistoryManager() {
        links = new HashMap<>();

    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> returnList = new ArrayList<>();
        Node p = head;
        while (p != null) {
            returnList.add(p.data);
            p = p.next;
        }
        return returnList;
    }

    @Override
    public <T extends Task> void add(T task) {
        if (links.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(long id) {
        if (links.containsKey(id)) {
            removeNode(links.get(id));
        }

    }


    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newTail = new Node(oldTail, task, null);

        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
        links.put(task.getId(), newTail);

    }

    private void removeNode(Node node) {
        links.remove(node.data.getId());
        if (head == tail) {
            head = null;
            tail = null;
        } else if (node == tail) {
            tail = node.prev;
            tail.next = null;
        } else if (node == head) {
            head = node.next;
            head.prev = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

    }

    public static class Node {

        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.prev = prev;
            this.data = data;
            this.next = next;

        }

    }
}
