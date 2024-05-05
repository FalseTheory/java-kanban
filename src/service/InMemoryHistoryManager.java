package service;

import model.Task;
import utils.Node;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Long, Node<Task>> links;
    private Node<Task> head;
    private Node<Task> tail;


    public InMemoryHistoryManager() {
        links = new HashMap<>();

    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public <T extends Task> void add(T task) {
        linkLast(task);
    }

    @Override
    public void remove(long id) {
        removeNode(links.get(id));
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> returnList = new ArrayList<>();
        Node<Task> p = head;
        while (p != null) {
            returnList.add(p.data);
            p = p.next;
        }
        return returnList;
    }

    private void linkLast(Task task) {
        if (links.containsKey(task.getId())) {
            remove(task.getId());
        }
        final Node<Task> oldTail = tail;
        final Node<Task> newTail = new Node<>(oldTail, task, null);

        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
        links.put(task.getId(), newTail);
    }

    private void removeNode(Node<Task> node) {
        links.remove(node.data.getId());
        if (head == tail) {
            head = null;
            tail = null;
        } else if (node == tail) {
            tail = node.prev;
        } else if (node == head) {
            head = node.next;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

    }
}
