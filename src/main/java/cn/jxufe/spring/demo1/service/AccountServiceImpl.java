package cn.jxufe.spring.demo1.service;

import cn.jxufe.spring.demo1.dao.AccountDao;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Created by Zhuang on 2018/4/23.
 * 转账案例的业务层的实现类
 */
public class AccountServiceImpl implements AccountService {
    //注入DAO类
    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    //注入事务管理的模板:
    private TransactionTemplate transactionTemplate;

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }


    @Override
    public void transfer(final String out,final String in,final Double money) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                accountDao.outMoney(out,money);
                //int i = 1 / 0;
                accountDao.inMoney(in,money);
            }
        });
    }
}
