package cn.jxufe.spring.demo2.service;

import cn.jxufe.spring.demo2.dao.AccountDao;
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

    @Override
    public void transfer(String out,String in,Double money) {
        accountDao.outMoney(out,money);
        //异常:
        //int i = 1 / 0;
        accountDao.inMoney(in,money);
    }
}
