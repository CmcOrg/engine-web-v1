package com.cmcorg.engine.web.datasource.util;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.function.Supplier;

@Component
public class TransactionUtil {

    private static DataSourceTransactionManager dataSourceTransactionManager;
    private static TransactionDefinition transactionDefinition;

    public TransactionUtil(DataSourceTransactionManager dataSourceTransactionManager,
        TransactionDefinition transactionDefinition) {
        TransactionUtil.dataSourceTransactionManager = dataSourceTransactionManager;
        TransactionUtil.transactionDefinition = transactionDefinition;
    }

    /**
     * 携带事务，执行
     */
    public static <T> T transactionExec(Supplier<T> supplier) {

        // 开启事务
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);

        try {

            T resObject = supplier.get();

            dataSourceTransactionManager.commit(transactionStatus); // 提交

            return resObject;

        } catch (Exception e) {

            dataSourceTransactionManager.rollback(transactionStatus); // 回滚
            throw e;

        }

    }

}
