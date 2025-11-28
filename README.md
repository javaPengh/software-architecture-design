# 电影院线上订票系统

## 项目简介
面向用户的在线电影票预订系统，提供票务查询、选座购票、支付、用户管理等功能。

## 核心功能
- 🎬 电影场次与座位实时查询
- 💺 在线选座与支付宝沙箱支付
- 👤 用户注册/登录/个人信息管理
- 📦 订单生成、查看与取消

---

## 🔧 安装与配置指南

### 前置要求
| 环境       | 版本要求       |
|------------|---------------|
| JDK        | 17+           |
| Node.js    | 16+           |
| Maven      | 3.6+          |
| GreatSQL   | 8.0+ (兼容MySQL) |

### 后端配置
1. **数据库设置**
   执行 `src/main/resources/sql/table.sql` 文件中的SQL语句。
2. **修改数据库连接**
   编辑 `src/main/resources/application.yaml`：
   ```yaml
   spring:
    datasource:
     type: com.alibaba.druid.pool.DruidDataSource
     druid:
      # 192.168.44.135:3306要修改为自己数据库对应的ip地址和端口，onlinebooking_system是数据库名
      url: jdbc:mysql://192.168.44.135:3306/onlinebooking_system
      username: root # 改成你自己的账户名
      password: '#@Cxr7725' # 改成你自己的密码
      driver-class-name: com.mysql.cj.jdbc.Driver # mysql对应驱动
   ```

3. **支付宝沙箱配置**
    - 修改 `src/main/resources/application.yaml` 中的支付回调地址：
      ```yaml
      # 支付成功回调地址（http://f82f57ab.natappfree.cc地址是localhost:8080映射到公网上的地址
      notifyUrl:  http://f82f57ab.natappfree.cc/alipay/notify
      ```

    - 获取公网访问地址：
        - 使用 **Natapp**：
            1. 访问 [natapp.cn](https://natapp.cn) 注册账号
            2. 购买免费隧道 → 配置隧道端口为 `8080`
            3. 启动客户端获取公网地址（如 `http://xxxxx.natappfree.cc`）

### 前端配置
1. 安装依赖（使用国内镜像加速）：
   ```bash
   cd online-booking-system
   npm install --registry=https://registry.npmmirror.com
   ```

## 🚀 启动项目
### 后端启动
1. 通过IDE运行主类：
   `src/main/java/org.zzu/SpringbootHeadlineApplication.java`
2. 或使用Maven命令：
   ```bash
   mvn spring-boot:run
   ```

### 前端启动
```bash
cd online-booking-system
npm run dev
```

> ✅ 成功启动后访问：  
> 前端：http://localhost:5173  
> 后端API：http://localhost:8080  
> 用户账号：user  密码：Test666...  
> 管理员账号：admin  密码：Test666...
---

## 📂 项目结构概览
### 前端核心
```
online-booking-system/       # 项目根目录
├── node_modules/            # npm依赖库目录（自动生成）
├── public/                  # 公共静态资源目录（直接拷贝到构建输出）
│   ├── assets/              # 公共资源文件
│   ├── images/              # 公共图片资源
│   └── vite.svg             # Vite框架的Logo文件
├── src/                     # 核心源代码目录
│   ├── assets/              # 本地静态资源（图片/字体等）
│   │   └── vue.svg          # Vue框架的Logo文件
│   ├── axios/               # Axios HTTP请求相关配置
│   │   └── axios.js         # Axios实例和拦截器配置文件
│   ├── components/          # 可复用Vue组件目录
│   │   ├── Aside.vue        # 侧边栏组件
│   │   ├── Captcha.vue      # 验证码组件
│   │   ├── Hall.vue         # 影厅信息管理页面组件
│   │   ├── Header.vue       # 页面头部组件
│   │   ├── Home.vue         # 首页组件
│   │   ├── Login.vue        # 登录页面组件
│   │   ├── Main.vue         # 主要内容区域组件
│   │   ├── Movie.vue        # 电影信息管理页面组件
│   │   ├── MyOrders.vue     # 我的订单页面组件
│   │   ├── Register.vue     # 注册页面组件
│   │   ├── Screening.vue    # 排片页面组件
│   │   └── TicketSearch.vue # 购票查询页面组件
│   ├── router/              # 路由配置目录
│   │   └── router.js        # Vue Router路由配置文件
│   ├── store/               # 状态管理目录（Pinia）
│   ├── App.vue              # Vue应用根组件
│   └── main.js              # 应用入口文件（初始化Vue实例）
├── .gitignore               # Git忽略规则配置
├── index.html               # 主HTML入口文件
├── LICENSE                  # 项目许可证文件
├── package.json             # npm项目配置和依赖声明
├── package-lock.json        # 依赖版本锁定文件
└── vite.config.js           # Vite构建工具配置文件
```

### 后端核心
```
src/
├── main/
│   ├── java/
│   │   └── org.zzu/
│   │       ├── config/                      # 项目配置类
│   │       │   ├── AlipayConfig.java        // 支付宝支付相关配置
│   │       │   └── WebMvcConfig.java        // Spring MVC配置（拦截器注册等）
│   │       │
│   │       ├── controller/                  # 控制器层（处理HTTP请求）
│   │       │   ├── AlipayController.java    // 支付宝支付接口
│   │       │   ├── HallController.java      // 影厅管理接口
│   │       │   ├── MovieController.java     // 电影管理接口
│   │       │   ├── ScreeningController.java // 场次管理接口
│   │       │   ├── TicketController.java    // 票务管理接口
│   │       │   └── UserController.java      // 用户管理接口
│   │       │
│   │       ├── filter/                      # 过滤器/拦截器
│   │       │   └── LoginProtectInterceptor.java // 登录保护拦截器（验证JWT）
│   │       │
│   │       ├── mapper/                      # MyBatis Mapper接口
│   │       │   ├── HallMapper.java          // 影厅数据库操作
│   │       │   ├── MovieMapper.java         // 电影数据库操作
│   │       │   ├── ScreeningMapper.java     // 放映场次数据库操作
│   │       │   ├── TicketMapper.java        // 票务数据库操作
│   │       │   └── UserMapper.java          // 用户数据库操作
│   │       │
│   │       ├── pojo/                        # 数据模型对象
│   │       │   ├── ChangePasswordDto.java   // 修改密码DTO
│   │       │   ├── Hall.java                // 影厅实体
│   │       │   ├── LoginDto.java            // 登录请求DTO
│   │       │   ├── Movie.java               // 电影实体
│   │       │   ├── PortalVo.java            // 数据传输实体
│   │       │   ├── Screening.java           // 场次实体
│   │       │   ├── ScreeningDto.java        // 场次查询DTO
│   │       │   ├── Ticket.java              // 票务实体
│   │       │   ├── TicketDto.java           // 购票请求DTO
│   │       │   └── User.java                // 用户实体
│   │       │
│   │       ├── service/                     # 服务层
│   │       │   ├── impl/                    // 服务实现类
│   │       │   │   ├── HallServiceImpl.java
│   │       │   │   ├── MovieServiceImpl.java
│   │       │   │   ├── ScreeningServiceImpl.java
│   │       │   │   ├── TicketServiceImpl.java
│   │       │   │   └── UserServiceImpl.java
│   │       │   │
│   │       │   ├── HallService.java         // 影厅服务接口
│   │       │   ├── MovieService.java        // 电影服务接口
│   │       │   ├── ScreeningService.java     // 场次服务接口
│   │       │   ├── TicketService.java       // 票务服务接口
│   │       │   └── UserService.java         // 用户服务接口
│   │       │
│   │       ├── utils/                       # 工具类
│   │       │   ├── JwtHelper.java           // JWT令牌工具
│   │       │   ├── MD5Util.java             // MD5加密工具
│   │       │   ├── Result.java              // 统一响应封装
│   │       │   ├── ResultCodeEnum.java      // 响应状态码枚举
│   │       │   └── TransPage.java           // 分页数据转换工具
│   │       │
│   │       └── SpringbootHeadlineApplication.java # 主启动类
│   │
│   └── resources/                           # 资源文件
│       ├── mapper/                          // MyBatis XML映射文件
│       │   ├── HallMapper.xml
│       │   ├── MovieMapper.xml
│       │   ├── ScreeningMapper.xml
│       │   ├── TicketMapper.xml
│       │   └── UserMapper.xml
│       │
│       ├── sql/                             // SQL脚本
│       │   └── table.sql                    // 数据库表结构
│       │
│       └── application.yaml                 // 主配置文件（数据源/环境配置）
```

---

## ⚠️ 常见问题
1. **依赖安装失败**
    - 切换npm镜像源：`npm config set registry https://registry.npmmirror.com`
    - Maven镜像配置：在`settings.xml`中添加阿里云镜像

2. **支付宝回调失败**
    - 确保Natapp映射端口与后端服务端口一致（默认8080）
    - 每次重启Natapp后需更新 `notifyUrl`

3. **数据库连接异常**
    - 检查 `application.yaml` 中的数据库名称和权限
    - 确认mysql服务已启动：`systemctl status mysqld`

4. **支付宝异步回调失败，订单状态未被更新为已支付**
    
- natapp映射的本地地址不是127.0.0.1:8080，未收到post请求。
    
5. **支付宝订单状态异常**
    - 支付宝商户订单号不能重复，在其他机器上使用时已被占用，在数据库中将ticket表自动递增tid加个几百或上千均可。如tid=43被占用，设置之后的tid从104开始递增，订单即可正常显示。
      ```sql
       ALTER TABLE ticket AUTO_INCREMENT = n;
      ```



## 软件架构作业由于新增技术需添加的操作

注意原来的项目结构已经变更，我创建了一个old_project模块，把原来的src目录移进去了

1、启动项目前需下载nacos服务启动服务端[Nacos 快速开始 | Nacos 官网](https://nacos.io/docs/latest/quickstart/quick-start/?spm=5238cd80.2ef5001f.0.0.3f613b7c9rQUrs)

2、访问nacos控制台http://localhost:8848/nacos，在dev环境添加2个配置文件： online-booking-system.yaml和membership-service.yaml，在old_project/src/main/resources/NacosConfig下

3、完成以上操作可以正常启动项目