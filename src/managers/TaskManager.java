package managers;

import java.util.HashMap;


public class TaskManager {
    private static long count = 0;

    private static HashMap<Long, Object> taskList;


    public static long getCount() {
        return ++count;
    }
}
