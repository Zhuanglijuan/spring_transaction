import cn.jxufe.spring.demo1.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by Zhuang on 2018/4/23.
 * 转账案例的测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringDemo1 {
    //测试业务层类
    @Resource(name = "accountService")
    private AccountService accountService;
    @Test
    public void demo1(){
        accountService.transfer("aaa","bbb",200d);
    }
}
