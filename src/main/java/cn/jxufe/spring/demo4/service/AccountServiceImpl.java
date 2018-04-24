package cn.jxufe.spring.demo4.service;

import cn.jxufe.spring.demo4.dao.AccountDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Zhuang on 2018/4/23.
 * 转账案例的业务层的实现类
 */

/**
 * @Transactional注解中的属性
 * propagation        :事物的传播行为
 * isolation          :事务的隔离级别
 * readOnly           :只读
 * rollback-for       :发生哪些异常回滚事务
 * no-rollback-for    :发生哪些异常事务不回滚
 */
@Transactional
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
        int i = 1 / 0;
        accountDao.inMoney(in,money);
    }
}
