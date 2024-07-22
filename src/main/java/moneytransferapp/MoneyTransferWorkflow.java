// @@@SNIPSTART money-transfer-java-workflow-interface
package moneytransferapp;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MoneyTransferWorkflow {
    // The Workflow Execution that starts this method can be initiated from code or
    // from the 'temporal' CLI utility.
    @WorkflowMethod
    void transfer(TransactionDetails transaction);
}
// @@@SNIPEND
