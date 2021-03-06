package cn.jxufe.spring.demo4.dao;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * Created by Zhuang on 2018/4/23.
 * 转账案例的DAO层的实现类
 */
public class AccountDaoImpl extends JdbcDaoSupport implements AccountDao {
    @Override
    public void outMoney(String out, Double money) {
        String sql = "update account set money = money - ? where name = ?";
        this.getJdbcTemplate().update(sql,money,out);
    }

    @Override
    public void inMoney(String in, Double money) {
        String sql = "update account set money = money + ? where name = ?";
        this.getJdbcTemplate().update(sql,money,in);
    }
}
