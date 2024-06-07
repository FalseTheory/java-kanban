package service;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;


@DisplayName("Менеджер задач")
public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {


    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getInMemoryManager();
    }





}
