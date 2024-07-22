// @@@SNIPSTART money-transfer-java-activity-interface
package moneytransferapp;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AccountActivity {
    // Withdraw an amount of money from the source account
    @ActivityMethod
    void withdraw(String accountId, String referenceId, int amount);

    // Deposit an amount of money into the destination account
    @ActivityMethod
    void deposit(String accountId, String referenceId, int amount);

    // Compensate a failed deposit by refunding to the original account
    @ActivityMethod
    void refund(String accountId, String referenceId, int amount);
}
// @@@SNIPEND
