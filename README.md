# 概述
mybatis-pro是一个基于mybatis持久层框架，在原生mybatis框架基础上进行了深度封装，开发人员针对数据库的CRUD操作更加简单方便。java 持久层框架历史发展过程中，hibernate和ibtas(mybatis)是两个比较经典的框架，hibernate是从对象的角度去看数据库，所有数据库层面的表在hibernate看来都是一个个对象，若是对象自然就有一对多，一对一等等关系。hibernate在对象层面完全封装了数据库层面的操作。所以它是一完整的ORM工具。最大的好处就是开发人员能通过很少的代码量就完成数据库层面的操作，大大提示了开发效率，不需要去写很多JDBC代码。

但是数据库毕竟不能只当做对象来看来，它有自己的组织方式，有自己的索引机制，当你数据库量到一定程度的时候，hibernate就会相当吃力，因为当通过对象的方式来组织对象之间的关系，就会导致映射到数据库层面的SQL会非常的复杂，另外传统hibernate对数据库操作sql对上层来说是不可见的，对DBA、开发人员来说是不可见的，不利于sql优化。

恰恰这种复杂并不是数据库执行引擎所期待的时候，就会导致各种慢查询，故后来出现了ibtas(mybatis)这种半自动化的方式来解决性能问题。同数据库层面的交互还是通过具体的SQL，但是提供了一种机制来简化开发的工作量，也就是写JDBC的工作。

尤其在今天的互联网应用中，业务相对不是特别复杂，更多的是单表操作，在使用mybatis时候更多时候需要频繁的去写各种sql mapper，带来了很多重复习性工作。base-mybatis针对这种场景，支持全自动化ORM和半自动化ORM，借助于JPA注解，简单表操作可以直接使用BaseDao的方法进行操作，复杂关联查询可以继续写sql mapper。同时具有 Hibernate 优点 & Mybatis 优点功能，适用于承认以 SQL 为中心，同时又需求工具能自动能生成大量常用的 SQL 的应用。
# **功能特性**

1.  支持全自动化ORM和半自动化ORM，简单表操作可以直接使用BaseDao的方法进行操作，复杂关联查询可以继续写sql mapper。
2.  二级缓存支持redis和memcached，只需要在数据库领域对象添加扩展注解@Cache即可。
3. 支持分库、分表sharding操作，只需要添加简单配置和相关注解即可完成复杂分库、分表操作，路由规则支持groovy表达式，支持读写分离和动态数据源。
4. 支持JPA注解，自动使用大量内置 SQL，轻易完成增删改查功能，节省 60% 的开发工作量。
5. 数据模型支持 Pojo，对象属性支持 Map/List/Enum/JSON 这种快速模型，简化了java enum的转换操作，json数据的序列化和反序列化可以自动实现，具体使用可以参考@FieldType注解。
6. 内置支持分页查询和批量操作，方便强大的分页功能，无须额外操作，二三行代码搞定分页，自动判断数据库，无须指定。
7. 查询条件表达式支持类似Criteria，查询条件更加符合人类自然语言，简单条件和排序可以使用Filter和Sorter进行构建。
8. 配套支持代码生成 pojo 类，减少代码编写工作量。
# **使用介绍**
## 1.添加maven依赖
```xml
<dependency>
<groupId>io.github.acticfox</groupId>
<artifactId>base-mybatis-starter</artifactId>
<version>1.0.0-SNAPSHOT</version>
</dependency>
```
## **2.添加datasource配置**
```properties
##application.properties add druid config##
spring.datasource.druid.url=jdbc:mysql://127.0.0.1:3306/student?characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true
spring.datasource.druid.username=root
spring.datasource.druid.password=root
spring.datasource.druid.max-active=600
spring.datasource.druid.min-idle=2
spring.datasource.druid.max-wait=60000
spring.datasource.druid.pool-prepared-statements=true
spring.datasource.druid.max-pool-prepared-statement-per-connection-size=20
spring.datasource.druid.validation-query=SELECT 'x'
#spring.datasource.druid.validation-query-timeout=
spring.datasource.druid.test-on-borrow=false
spring.datasource.druid.test-on-return=false
spring.datasource.druid.test-while-idle=false
spring.datasource.druid.time-between-eviction-runs-millis=3000
spring.datasource.druid.min-evictable-idle-time-millis=25000
#spring.datasource.druid.max-evictable-idle-time-millis=
spring.datasource.druid.filters=stat
```
## 3.定义业务模型对象
```java
@Data
public class User extends BaseObject {
private static final long serialVersionUID = 888331980200026820L;

@Id
@Column(name = "uid", updatable = false)
@GeneratedValue(strategy = GenerationType.AUTO, generator = "system-uuid")
private String            guid;
private String            name;
private String            pass;
private int               age;
private Date              createTime;
private Date              updateTime;
@FieldType(FieldTypeEnum.JSON)
private Extend            extend;
@FieldType(FieldTypeEnum.IntEnum)
private Type              type;
}
```

**支持JAP相关注解**

| **注解名称** | **注解属性** | **备注** |
| --- | --- | --- |
| @Table | name属性用于表名定义，比如UserItem对象默认对应表名user_item | |
| @Id | 用于主键定义 | |
| @Column | name列名定义，比如createTime属性默认对应默认列名create_time,也可以用于name="cr_date"定义别名 | |
| @GeneratedValue | 主要用于主键生成，默认为自增，strategy = GenerationType.AUTO, generator = "system-uuid"表示主键使用uuid | |


**支持自定义注解**

| **注解名称** | **注解属性** | **备注** |
| --- | --- | --- |
| @FieldType | value值使用FieldTypeEnum枚举，orm映射支持枚举和int/string code码进行映射，同时支持json序列化和反序列化 | |
| @CacheType | value值使用CacheTypeEnum枚举，默认使用LRU_Redis，基于redis的二级缓存 | |


如果属性使用FieldType，需要定义java枚举，示例如下：
```java
public enum Type implements IntEnum<Type> {
A(0,"错误"),
B(1,"正确");

private int code;

private Type(int code) {
this.code = code;
}

@Override
public int getCode() {
return code;
}
}
```
## **4.定义DAO**
```java
@Repository
public class UserSimpleDao extends BaseDao<User> {

    //新增用户
    public int insertUser(User user) {
    return insert(user);
    }
    //根据用户uid查询用户列表
    public User queryUserById(String uid) {
    // 绑定参数
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", uid);
        // 执行查询
        return (User) queryDAO.executeForObject("user.getUser", param, Map.class);
    }
	//保存用户
    public int saveUser(User user) {
    	return updateDAO.execute("user.update", user);
    }
	//查询用户列表
    public List<User> queryUserList(String name, Type type) {
        Filter filter = FilterFactory.getSimpleFilter("type", type);
        filter.addCondition("name", name);
        Sorter sort = SorterFactory.getSimpleSort("createTime", SortOrder.DESC);
        return queryList(filter, sort);
    }
}    
```



- QueryDAO： 数据查询接口，提供了丰富的通用查询接口
- UpdateDAO ：数据更新（插入、更新、删除、执行存储过程）接口
- Sorter： Sorter主要用于规范面向对象的查询排序条件,排序分为两部分：字段名、方向符，可以通过addSort方法不断累加排序方法返回的还是 当前 Sorter的实例，其操作方式与Filter方式相同 本接口是规范以面向对象的方式对数据库查询进行排序。
-  Filter： Filter用于构造查询条件，规范面向对象的查询过滤条件，是对单表的过滤操作 过滤器由四部分组成：字段名、值、操作符与关系符，可以通过addCondition方法 不断累加查询条件，该方法的返回值的还是当前的Filter的实例，所以可以象StringBuffer 一样的操作。

注意:目前的Filter过滤器不支持between操作，但可以通过调用两次addCondition方法来实现 该功能。同时不支持左、右外连接与子查询，可通过相应的ORM在具体的DAO类中实现该功能。
最后，为了规范编码没有对addCondition方法做多余的过载，这也就是意味着对 于该方法中的参数val一定要对应相应的POJO中属性的类型。

注意：mybatis-pro组件既可以使用ORM方式DAO查询，也可以使用自定义mapper，在classpath根目录下添加mapper文件夹，添加*-manual.xml sqlmap文件。

**总结：每个表对应数据领域对象DAO需要继承BaseDao，BaseDao提供了很多通用的CRUD操作。**

# 
高级功能
## **1.mybatis二级缓存**
mybatis二级缓存默认支持oscache、ecache，oscache、ecache都是java进程级缓存，相对于memcached、redis这种分布式缓存，存在很多弊端，java缓存受限于java GC影响，内存分配与回收不可控，oscache、ecache虽然支持集群模式，数据一致性在某些极端场景下容易出问题。
### **使用说明**
#### **1.添加缓存配置**
开启redis二级缓存 在application.properties添加如下配置：
```properties
mybatis.redis.host=127.0.0.1
mybatis.redis.port=6379
mybatis.redis.connectionTimeout=5000
mybatis.redis.password=
mybatis.redis.database=0
mybatis.redis.keyPrefix=_mybatis_
```
#### **2.使用方法**
1)方法一：sqlmap中开启二级缓存
```xml
<cache type="com.github.acticfox.mybatis.plugin.cache.redis.MybatisRedisCache"/>
```
2)方法二：实体对象添加注解@CacheType
```java
@CacheType(CacheTypeEnum.LRU_Redis)
public class User{
........
}
```
