import cn.jxufe.spring.demo2.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by Zhuang on 2018/4/23.
 * Spring的声明式事务管理的方式一的测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext2.xml")
public class SpringDemo2 {
    /**
     * 注入代理类:因为代理类进行了增强的操作
     */

    //测试业务层类
    @Resource(name = "accountServiceProxy")
    //@Resource(name = "accountService")
    private AccountService accountService;
    @Test
    /**
     * 转账案例:
     */
    public void demo2(){
        accountService.transfer("aaa","bbb",200d);
    }

}
