## 一、项目介绍
使用SpringBoot+JDK21实现的一个与银行系统内的交易管理相关的简单应用程序

## 二、代码组织结构
``` lua
bank_transaction_system
├── controller -- 控制器层转发处理
├── dao -- MyBatis数据交互操作
├── domain -- 定义实体类和接口返回结构公共类
├── service -- 核心业务逻辑处理
├── util -- 常量定义及异常统一处理等工具类定义
└── BankTransactionSystemApplication -- 启动入口

```
## 三、涉及技术栈介绍
| 技术                   | 说明            | 官网                                          |
|----------------------|---------------| --------------------------------------------- |
| SpringBoot           | Web应用开发框架     | https://spring.io/projects/spring-boot        |
| MyBatis              | ORM框架         | http://www.mybatis.org/mybatis-3/zh/index.html |
| MySQL8               | DB关系型数据库      | https://www.mysql.com/ |
| Caffeine              | 一个用于Java应用程序的高性能缓存框架       | https://github.com/ben-manes/caffeine          |
| Druid                | 数据库连接池        | https://github.com/alibaba/druid              |
| Lombok               | Java语言增强库     | https://github.com/rzwitserloot/lombok        |
| Hutool               | Java工具类库      | https://github.com/looly/hutool               |
| PageHelper           | MyBatis物理分页插件 | http://git.oschina.net/free/Mybatis_PageHelper |
| Hibernator-Validator | 验证框架          | http://hibernate.org/validator                |

## 四、表结构与接口设计
- 表结构
```sql
CREATE TABLE `bank_transaction` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `serial_number` char(36) NOT NULL COMMENT '交易流水号',
  `account_number` char(16) NOT NULL COMMENT '卡号',
  `amount` decimal(15,2) NOT NULL COMMENT '交易金额(必须大于0)',
  `type` enum('DEPOSIT','WITHDRAWAL','TRANSFER') NOT NULL COMMENT '交易类型:存款/取款/转账',
  `description` varchar(255) DEFAULT NULL COMMENT '交易描述信息',
  `timestamp` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '交易时间戳(精确到微秒)',
  `category` varchar(50) DEFAULT NULL COMMENT '交易分类(如:工资、购物等)',
  `status` enum('PENDING','COMPLETED','FAILED') NOT NULL DEFAULT 'PENDING' COMMENT '交易状态:处理中/已完成/已失败',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '记录创建时间',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '记录最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='银行交易表';
```
- 接口设计
> 遵循RESTful API设计原则实现了添加交易信息、修改交易信息、删除交易信息、交易信息列表分页查询四个接口

  + 路由：/bankTransaction
  
  + 接口列表：
  
![img_28.png](img_28.png)

## 五、接口功能测试
- 新增交易信息接口

![img_29.png](img_29.png)

- 更新交易信息接口

![img_30.png](img_30.png)

- 删除交易信息接口

![img_31.png](img_31.png)

- 分页查询交易信息接口

![img_33.png](img_33.png)


## 六、单元测试
### 1、测试结果
![img_1.png](img_1.png)
### 2、测试覆盖率
![img_2.png](img_2.png)

## 七、打包部署至K8S集群
### 1、应用打包
- 首先检查application-dev.properties文件中的MySQL配置信息，改成K8S集群中网络互通的MySQL地址
- 然后切到项目根目录下执行`mvn clean package -X` 命令对应用进行打包，执行完命令后可以看到target目录下生成了一个bank_transaction_system-0.0.1-SNAPSHOT.jar的包

### 2、将jar包和项目根目录下的Dockerfile、k8s_deployment.yaml和k8s_service.yaml 一起上传至k8s所在master节点/data/deploy/目录下
```shell

# Dockerfile
FROM openjdk:21-ea-9
ARG JAR_FILE=/data/deploy/*.jar # 注意jar存放的位置，在k8s在master节点上构建镜像前检查jar包位置
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```
```shell

# k8s_deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bank-transaction-system-deployment
  namespace: default
spec:
  replicas: 1
  selector:
    matchLabels:
      app: bank-transaction-system
  template:
    metadata:
      labels:
        app: bank-transaction-system
    spec:
      containers:
        - name: bank-transaction-system-container
          image: bank_trasaction_system_image_x86_jdk21:latest
          imagePullPolicy: Never # 使用本地镜像，不走远程docker仓库拉取镜像
          ports:
            - containerPort: 8088
          resources:  # 添加资源限制
            requests:
              memory: "512Mi"
              cpu: "1"
            limits:
              memory: "4Gi"
              cpu: "2"
```
```shell

# k8s_service.yaml
apiVersion: v1
kind: Service
metadata:
  name: bank-transaction-system-service
  namespace: default  # 指定命名空间
spec:
  selector:
    app: bank-transaction-system-app
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8088
  type: LoadBalancer
```

### 2、构建镜像
k8s所在master节点的/data/deploy/目录下执行以下命令
```shell

docker build -t bank_trasaction_system_image_x86_jdk21:latest .
```

### 3、k8s所在master节点上加载配置，启动应用pod
```shell

kubectl apply -f k8s_deployment.yaml
kubectl apply -f k8s_service.yaml
```
### 4、查看pod和svc是否OK
![img.png](img.png)

## 八、接口压测
> 主要对查询接口进行性能测试

### 压测方式：
使用Apache ab工具：命令如下
`ab -n1000 -c10 http://localhost:8080/homeWork/api/v1/bankTransaction?pageNum=1"&"pageSize=2 `
记录每次压测数据：
### 1、总请求量为1000次，并发为10下的测试结果
- 第一次

![img_5.png](img_5.png)

- 第二次

![img_6.png](img_6.png)

- 第三次

![img_7.png](img_7.png)

### 2、总请求量为1000次，并发为20下的测试结果
- 第一次

![img_8.png](img_8.png)

- 第二次

![img_9.png](img_9.png)

- 第三次

![img_10.png](img_10.png)

### 3、总请求量为1000次，并发为30下的测试结果
- 第一次

![img_11.png](img_11.png)

- 第二次

![img_12.png](img_12.png)

- 第三次

![img_13.png](img_13.png)

### 4、总请求量为1000次，并发为40下的测试结果
- 第一次

![img_14.png](img_14.png)

- 第二次

![img_15.png](img_15.png)

- 第三次

![img_16.png](img_16.png)

### 5、总请求量为1000次，并发为50下的测试结果
- 第一次

![img_17.png](img_17.png)

- 第二次

![img_18.png](img_18.png)

- 第三次

![img_19.png](img_19.png)

### 6、总请求量为1000次，并发为80下的测试结果
- 第一次

![img_20.png](img_20.png)

- 第二次

![img_21.png](img_21.png)

### 7、总请求量为1000次，并发为100下的测试结果
- 第一次

![img_22.png](img_22.png)

- 第二次

![img_23.png](img_23.png)

### 8、总请求量为1000次，并发为150下的测试结果
- 第一次

![img_24.png](img_24.png)

- 第二次

![img_25.png](img_25.png)

### 9、总请求量为1000次，并发为200下的测试结果
- 第一次

![img_26.png](img_26.png)

- 第二次

![img_27.png](img_27.png)
> 总结：在tomcat最大线程数为200，单节点配置为2核4C下，查询API的QPS可达11000-16000，使用本地缓存加速处理，提高了响应速度。