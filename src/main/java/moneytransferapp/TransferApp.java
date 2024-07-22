// @@@SNIPSTART money-transfer-java-initiate-transfer
package moneytransferapp;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.concurrent.ThreadLocalRandom;

public class TransferApp {
    private static final SecureRandom random;

    static {
        // Seed the random number generator with nano date
        random = new SecureRandom();
        random.setSeed(Instant.now().getNano());
    }

    public static String randomAccountIdentifier() {
        return IntStream.range(0, 9)
                .mapToObj(i -> String.valueOf(random.nextInt(10)))
                .collect(Collectors.joining());
    }

    public static void main(String[] args) throws Exception {

        // In the Java SDK, a stub represents an element that participates in
        // Temporal orchestration and communicates using gRPC.

        // A WorkflowServiceStubs communicates with the Temporal front-end service.
        WorkflowServiceStubs serviceStub = WorkflowServiceStubs.newLocalServiceStubs();

        // A WorkflowClient wraps the stub.
        // It can be used to start, signal, query, cancel, and terminate Workflows.
        WorkflowClient client = WorkflowClient.newInstance(serviceStub);

        // Workflow options configure  Workflow stubs.
        // A WorkflowId prevents duplicate instances, which are removed.
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(Shared.MONEY_TRANSFER_TASK_QUEUE)
                .setWorkflowId("money-transfer-workflow")
                .build();

        // WorkflowStubs enable calls to methods as if the Workflow object is local
        // but actually perform a gRPC call to the Temporal Service.
        MoneyTransferWorkflow workflow = client.newWorkflowStub(MoneyTransferWorkflow.class, options);
        
        // Configure the details for this money transfer request
        String referenceId = UUID.randomUUID().toString().substring(0, 18);
        String fromAccount = randomAccountIdentifier();
        String toAccount = randomAccountIdentifier();
        int amountToTransfer = ThreadLocalRandom.current().nextInt(15, 75);
        TransactionDetails transaction = new CoreTransactionDetails(fromAccount, toAccount, referenceId, amountToTransfer);

        // Perform asynchronous execution.
        // This process exits after making this call and printing details.
        WorkflowExecution we = WorkflowClient.start(workflow::transfer, transaction);

        System.out.printf("\nMONEY TRANSFER PROJECT\n\n");
        System.out.printf("Initiating transfer of $%d from [Account %s] to [Account %s].\n\n",
                          amountToTransfer, fromAccount, toAccount);
        System.out.printf("[WorkflowID: %s]\n[RunID: %s]\n[Transaction Reference: %s]\n\n", we.getWorkflowId(), we.getRunId(), referenceId);
        System.exit(0);
    }
}
// @@@SNIPEND
