package transfermoneyapp;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TransferMoneyWorkflowTest {

    private TestWorkflowEnvironment testEnv;
    private Worker worker;
    private WorkflowClient workflowClient;

    @Before
    public void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        worker = testEnv.newWorker(Shared.TRANSFER_MONEY_TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(TransferMoneyWorkflowImpl.class);
        workflowClient = testEnv.getWorkflowClient();
    }

    @After
    public void tearDown() {
        testEnv.close();
    }

    @Test
    public void testTransfer() {
        AccountActivity activities = mock(AccountActivity.class);
        worker.registerActivitiesImplementations(activities);
        testEnv.start();
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(Shared.TRANSFER_MONEY_TASK_QUEUE)
                .build();
        TransferMoneyWorkflow workflow = workflowClient.newWorkflowStub(TransferMoneyWorkflow.class, options);
        long start = testEnv.currentTimeMillis();
        workflow.transfer("account1", "account2", "reference1", 1.23);
        verify(activities).withdraw(eq("account1"), eq("reference1"), eq(1.23));
        verify(activities).deposit(eq("account2"), eq("reference1"), eq(1.23));
        long duration = testEnv.currentTimeMillis() - start;
        System.out.println("Duration: " + duration);
    }
}



