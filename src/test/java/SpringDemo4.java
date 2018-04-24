import cn.jxufe.spring.demo4.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;


/**
 * Created by Zhuang on 2018/4/23.
 * Spring声明式事务管理的方式三:基于注解的事务管理的方式
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext4.xml")
public class SpringDemo4 {
    @Resource(name = "accountService")
    private AccountService accountService;
    @Test
    /**
     * 转账案例:
     */
    public void demo4(){
        accountService.transfer("aaa","bbb",200d);
    }
}