package android.helper.database.imp;

import android.helper.database.util.DatabaseHelper;

/**
 * Created by lqnhu on 6/21/15.
 */
public interface TransactionAction {
    void execute(DatabaseHelper helper) throws Exception;
}
