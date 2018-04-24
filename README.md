# Spring事务管理 #

## 一、什么是事务 ##
事务指的是逻辑上的一组操作，这组操作要么全部成功，要么全部失败。

## 二、事务的特性 ##
原子性、一致性、隔离性、持久性
1.	原子性：事务是一个不可分割的工作单位，事务中的操作要么都发生，要么都不发生。
2.	一致性：事务前后数据的完整性必须保持一致。
3.	隔离性：指的是多个用户并发访问数据库时，一个用户的事务不能被其他用户的事务所干扰，多个并发事物之间数据要相互隔离。
4.	持久性：一个事物一旦被提交了，他对数据库中数据的改变就是永久性的，即使数据库发生故障也不应该对其有任何影响。


## 三、Sping接口介绍 ##

1. PlatformTransactionManager 事务管理器
Spring为不同的持久化框架提供了不同PlatformTransactionManager接口实现

![](https://github.com/Zhuanglijuan/spring_transaction/blob/master/imgs/1.jpg)

	一般使用DatasourceTransactionManager和HibernateTransactionManager接口
2. TransactionDefinition 事务定义信息（隔离、传播、超时、只读）
如果不考虑隔离性，会引发安全问题如下:
**脏读、不可重复读、幻读。**

	**脏读：**
一个事务读取到了另一个事物改写但还未提交的数据，如果这些数据被回滚，则读到的数据是无效的。

	**不可重复读：**
在同一个事务中，多次读取同一数据返回的结果有所不同。

	**幻读：**
一个事务读取了几行记录后，另一个事务插入了一些记录，再后来的查询中，第一个事务就会发现有些原来没有的记录。产生了幻读现象。

	**数据库提供了四个隔离界别**

	[https://github.com/Zhuanglijuan/spring_transaction/blob/master/imgs/2.jpg](https://github.com/Zhuanglijuan/spring_transaction/blob/master/imgs/2.jpg)
 
	MySQL默认采用REPEATABLE_READ隔离级别
	Oracle默认READ_COMMITTED隔离界别
	 
	出现复杂情况:调用Service1.aaa()和Service2中的bbb(),才能给完成一个

	事务的传播行为:解决业务层方法之间的相互调用的问题
	事务的传播行为有七种:如图
[https://github.com/Zhuanglijuan/spring_transaction/blob/master/imgs/3.png](https://github.com/Zhuanglijuan/spring_transaction/blob/master/imgs/3.png)
	
3.TransactionStatus 事务具体运行状态

转账为例环境搭建
1.	创建数据库，导入初始数据
			CREATE TABLE `account` (
			  `id` int(11) NOT NULL AUTO_INCREMENT,
			  `name` varchar(20) NOT NULL,
			  `money` double DEFAULT NULL,
			  PRIMARY KEY (`id`)
			) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
			INSERT INTO `account` VALUES ('1', 'aaa', '1000');
			INSERT INTO `account` VALUES ('2', 'bbb', '1000');
			INSERT INTO `account` VALUES ('3', 'ccc', '1000');
2.	导入基本jar包
			<dependencies>
			    <dependency>
			      <groupId>junit</groupId>
			      <artifactId>junit</artifactId>
			      <version>4.11</version>
			      <scope>test</scope>
			    </dependency>
			    <dependency>
			      <groupId>com.mchange</groupId>
			      <artifactId>c3p0</artifactId>
			      <version>0.9.5.2</version>
			    </dependency>
			    <dependency>
			      <groupId>aopalliance</groupId>
			      <artifactId>aopalliance</artifactId>
			      <version>1.0</version>
			    </dependency>
			    <dependency>
			      <groupId>commons-logging</groupId>
			      <artifactId>commons-logging</artifactId>
			      <version>1.1.1</version>
			    </dependency>
			    <dependency>
			      <groupId>log4j</groupId>
			      <artifactId>log4j</artifactId>
			      <version>1.2.17</version>
			    </dependency>
			    <dependency>
			      <groupId>org.aspectj</groupId>
			      <artifactId>aspectjweaver</artifactId>
			      <version>1.7.3</version>
			    </dependency>
			    <dependency>
			      <groupId>mysql</groupId>
			      <artifactId>mysql-connector-java</artifactId>
			      <version>5.1.6</version>
			    </dependency>
			    <dependency>
			      <groupId>org.springframework</groupId>
			      <artifactId>spring-aop</artifactId>
			      <version>4.3.11.RELEASE</version>
			    </dependency>
			    <dependency>
			      <groupId>org.springframework</groupId>
			      <artifactId>spring-beans</artifactId>
			      <version>4.3.11.RELEASE</version>
			    </dependency>
			    <dependency>
			      <groupId>org.springframework</groupId>
			      <artifactId>spring-context</artifactId>
			      <version>4.3.11.RELEASE</version>
			    </dependency>
			    <dependency>
			      <groupId>org.springframework</groupId>
			      <artifactId>spring-core</artifactId>
			      <version>4.3.11.RELEASE</version>
			    </dependency>
			    <dependency>
			      <groupId>org.springframework</groupId>
			      <artifactId>spring-expression</artifactId>
			      <version>4.3.11.RELEASE</version>
			    </dependency>
			    <dependency>
			      <groupId>org.springframework</groupId>
			      <artifactId>spring-jdbc</artifactId>
			      <version>4.3.10.RELEASE</version>
			    </dependency>
			    <dependency>
			      <groupId>org.springframework</groupId>
			      <artifactId>spring-test</artifactId>
			      <version>4.0.6.RELEASE</version>
			    </dependency>
			    <dependency>
			      <groupId>org.springframework</groupId>
			      <artifactId>spring-tx</artifactId>
			      <version>4.3.10.RELEASE</version>
			    </dependency>
			  </dependencies>
3.	面向接口编程 创建Service
```java
			/**
			 * Created by Zhuang on 2018/4/23.
			 * 转账案例的业务层接口
			 */
			public interface AccountService {
			    /**
			     *
			     * @param out   :转出账号
			     * @param in    :转入账号
			     * @param money :转账金额
			     */
			    public void transfer(String out,String in,Double money);
			}
```
```java
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
			    public void transfer(String out, String in, Double money) {
			        accountDao.outMoney(out,money);
			        accountDao.inMoney(in,money);
			    }
			}
```
4.	创建Dao层
```java
			/**
			 * Created by Zhuang on 2018/4/23.
			 * 转账案例的DAO层接口
			 */
			public interface AccountDao {
			    /**
			     *
			     * @param out   :转出账号
			     * @param money :转账金额
			     */
			    public void outMoney(String out,Double money);
			
			    /**
			     *
			     * @param in    :转入账号
			     * @param money :转账金额
			     */
			    public void inMoney(String in,Double money);
			}
```
```java
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
```

##  四、编程式的事务控制  ##
1. 配置事务管理器
		<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		        <property name="dataSource" ref="dataSource"/>
		</bean>	
2. 配置事务管理的模板:Spring为了简化事务管理的代码而提供的类
		<bean id="transactionTemplate" class="org.springframework.transaction.support.TransactionTemplate">
		        <property name="transactionManager" ref="transactionManager"/>
		</bean>
3. 向accountService类里注入事务管理的模板
		<!--配置业务层的类-->
		<bean id="accountService" class="cn.jxufe.spring.demo1.service.AccountServiceImpl">
		<property name="accountDao" ref="accountDao"/>
		<property name="transactionTemplate" ref="transactionTemplate"/>
		</bean>
4. 事务控制
```java
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
```
5. 测试类
```java
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
```

## 五、声明式事务管理 ##
1. 基于TransactionProxyFactoryBean的方式
	1. 配置事务管理器
		    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		        <property name="dataSource" ref="dataSource"/>
		    </bean>
	2. 配置业务层的代理
		    <bean id="accountServiceProxy" class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
		        <!--配置目标对象-->
		        <property name="target" ref="accountService"/>
		        <!--注入事务管理器-->
		        <property name="transactionManager" ref="transactionManager"/>
		        <!--注入事务属性-->
		        <property name="transactionAttributes" >
		            <props>
		                <!--
		                    prop的格式:
		                       * PROPAGATION :事物的传播行为
		                       * ISOLATION   :事务的隔离级别
		                       * readOnly     :只读
		                       * -Exception  :发生哪些异常回滚事务
		                       * +Exception  :发生哪些异常事务不回滚
		                -->
		                <prop key="transfer">PROPAGATION_REQUIRED</prop>
		            </props>
		        </property>
		    </bean>
	3. 测试类修改
	```java
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
	```
2. 基于AspectJ的XML方式的配置
	1. 配置事务通知
		    <tx:advice id="txAdvice" transaction-manager="transactionManager">
		        <tx:attributes>
		            <!--
		                   * PROPAGATION        :事物的传播行为
		                   * ISOLATION          :事务的隔离级别
		                   * readOnly           :只读
		                   * rollback-for       :发生哪些异常回滚事务
		                   * no-rollback-for    :发生哪些异常事务不回滚
		                   * timeout            :过期信息
		            -->
		            <tx:method name="transfer" propagation="REQUIRED"/>
		        </tx:attributes>
		    </tx:advice>
	2. 配置切面
	    <aop:config>
	        <!--配置切入点-->
	        <aop:pointcut id="pointcut1" expression="execution(* cn.jxufe.spring.demo3.service.AccountService+.*(..))"/>
	        <!--配置切面-->
	        <aop:advisor advice-ref="txAdvice" pointcut-ref="pointcut1"/>
	    </aop:config>
	3. 测试类
```java
	/**
	 * Created by Zhuang on 2018/4/23.
	 * Spring声明式事务管理的方式二:基于AspectJ的XML方式的配置
	 */
	@RunWith(SpringJUnit4ClassRunner.class)
	@ContextConfiguration("classpath:applicationContext3.xml")
	public class SpringDemo3 {
	    @Resource(name = "accountService")
	    private AccountService accountService;
	    @Test
	    /**
	     * 转账案例:
	     */
	    public void demo3(){
	        accountService.transfer("aaa","bbb",200d);
	    }
	}
```
3. 基于注解的事务管理的方式
	1. 配置事务管理器
		    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		        <property name="dataSource" ref="dataSource"/>
		    </bean>
	2. 开启注解事务
		    <tx:annotation-driven transaction-manager="transactionManager" />
	3. 业务层实现类
```java
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
```		