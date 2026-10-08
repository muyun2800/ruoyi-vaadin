<p align="center">
	<img alt="logo" src="https://oscimg.oschina.net/oscnet/up-d3d0a9303e11d522a06cd263f3079027715.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">RuoYi-Vaadin v3.9.1</h1>
<h4 align="center">基于SpringBoot+Vaadin前后端分离的Java快速开发框架</h4>
<p align="center">
	<a href="https://gitee.com/y_project/RuoYi-Vue/stargazers"><img src="https://gitee.com/y_project/RuoYi-Vue/badge/star.svg?theme=dark"></a>
	<a href="https://gitee.com/y_project/RuoYi-Vue"><img src="https://img.shields.io/badge/RuoYi-v3.9.1-brightgreen.svg"></a>
	<a href="https://gitee.com/y_project/RuoYi-Vue/blob/master/LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg"></a>
</p>

## 平台简介

若依是一套全部开源的快速开发平台，毫无保留给个人及企业免费使用。

**本项目是 [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue.git) 的派生版本，前端使用 [Vaadin](https://vaadin.com/) 替代了 Vue，后端保持 Spring Boot 架构。**

**前端实现参考了：[ruoyi-react](https://gitee.com/whiteshader/ruoyi-react)**

* **前端采用 Vaadin**（注意：不是 Vue），基于 Java 实现服务端渲染。使用了vaadin-antd、vaadin-antd-procomponents、vaadin-antd-chart等组件库。
* 后端采用Spring Boot、Spring Security、Redis & Jwt。
* 权限认证使用Jwt，支持多终端认证系统。
* 支持加载动态权限菜单，多方式轻松权限控制。
* 高效率开发，使用代码生成器可以一键生成前后端代码。
* **使用说明**：本项目作为普通的 Spring Boot 项目使用，无需前端构建工具，直接运行即可。

## 关于 Vaadin

### 什么是 Vaadin？

[Vaadin](https://vaadin.com/) 是一个开源的全栈 Java UI 框架，让您可以**完全使用 Java 构建现代化的 Web 应用程序**，无需编写 HTML、CSS 或 JavaScript。

### 核心优势

#### 1. 全栈 Java，无需切换项目

- **告别前后端分离的繁琐**：不需要在 Java 后端项目和 Vue/React 前端项目之间来回切换
- **统一技术栈**：前后端都使用 Java，团队只需要掌握一种语言
- **单一代码库**：所有代码都在一个项目中，简化了构建、部署和维护

#### 2. 代码量更少，效率更高

根据实际项目案例统计：

| 对比项 | 传统前后端分离 | 使用 Vaadin | 提升效果 |
|--------|----------------|-------------|----------|
| 代码量 | 需要编写 HTML/CSS/JS/REST API | 只需编写 Java | **减少 40%-60%** |
| 开发时间 | 前后端分别开发、联调 | 一体化开发 | **节省 30%-50%** |
| 维护成本 | 多项目、多技术栈 | 单一项目、单一技术栈 | **降低 50%+** |

**真实案例数据**：
- **HDI 保险集团**：从 450,000 行代码减少到 250,000 行，**代码量减少 44%**
- **Virsion 初创公司**：2 人团队节省了至少 1 个月的开发时间
- **qubIT**：15 人团队管理 4,000+ 页面的企业级应用
- **South Tyrol 政府**：7 人团队交付了 16 个生产模块，支持 116 个市政机构

#### 3. 像开发桌面应用一样简单

```java
// 创建一个表格，就像使用 Swing/JavaFX 一样简单
ProTable<User> table = new ProTable<>(User.class)
    .setColumns(
        new ProColumn<User>().setTitle("姓名").setDataIndex(User::getName),
        new ProColumn<User>().setTitle("邮箱").setDataIndex(User::getEmail)
    )
    .setItems(userService.findAll());

add(table);
```

#### 4. 企业级特性

- **安全性**：服务端渲染，最小化攻击面
- **可扩展性**：支持数千并发用户
- **丰富的组件库**：表格、表单、图表、地图等 40+ 组件
- **Spring Boot 集成**：无缝集成，开箱即用
- **响应式设计**：自动适配桌面、平板、手机

### 与传统前端开发对比

| 特性 | 传统方式（Vue/React） | Vaadin |
|------|----------------------|--------|
| 开发语言 | JavaScript/TypeScript + Java | 纯 Java |
| 项目数量 | 前端项目 + 后端项目 | 单一项目 |
| 需要掌握的技术 | HTML/CSS/JS/框架/REST API | Java |
| 前后端通信 | 需要编写 REST API 和 HTTP 调用 | 自动处理 |
| 状态管理 | Redux/Vuex/Pinia 等 | 服务端状态，无需额外管理 |
| 构建工具 | Webpack/Vite + Maven/Gradle | 仅 Maven/Gradle |
| 调试 | 浏览器 DevTools + IDE | 仅 IDE |
| 部署 | 前端构建 + 后端部署 | 单一 WAR/JAR 包 |

### 适合的场景

- **企业级管理系统**：ERP、CRM、OA 等
- **数据密集型应用**：报表、仪表盘、数据分析
- **内部工具**：快速开发企业内部系统
- **Java 团队**：团队主要技能是 Java，前端经验有限
- **追求效率**：希望快速交付、降低维护成本


## 内置功能

1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
13. 代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。
14. 系统接口：根据业务代码自动生成相关的api接口文档。
15. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
16. 缓存监控：对系统的缓存信息查询，命令统计等。
17. 在线构建器：拖动表单元素生成相应的HTML代码。
18. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。

## 在线体验

- admin/admin123  
- 陆陆续续收到一些打赏，为了更好的体验已用于演示服务器升级。谢谢各位小伙伴。

演示地址：http://vue.ruoyi.vip  
文档地址：http://doc.ruoyi.vip



 


## 打包
```
mvn clean package -DskipTests -Pproduction
```

## Vaadin-Antd 依赖

本项目使用 [vaadin-antd](https://gitee.com/muyun2800/vaadin-antd) 组件库，主要依赖如下：

```xml
<!-- Vaadin Spring Boot Starter -->
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>vaadin-spring-boot-starter</artifactId>
</dependency>

<!-- Vaadin Antd 基础组件 -->
<dependency>
    <groupId>io.gitee.muyun2800</groupId>
    <artifactId>vaadin-antd</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Vaadin Antd ProComponents 高级组件（表格、表单等） -->
<dependency>
    <groupId>io.gitee.muyun2800</groupId>
    <artifactId>vaadin-antd-procomponents</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Vaadin Antd Charts 图表组件 -->
<dependency>
    <groupId>io.gitee.muyun2800</groupId>
    <artifactId>vaadin-antd-charts</artifactId>
    <version>1.0.0</version>
</dependency>
```

## ListView 最简示例

以下是一个使用 `ProTable` 的 ListView 最简示例：

```java
package com.example.view;

import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.muyun.vaadin.antd.procomponents.table.data.FetchCallback;
import com.muyun.vaadin.antd.procomponents.table.data.FetchResult;
import com.muyun.vaadin.antd.procomponents.table.props.ProColumn;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "example/list", layout = MainLayoutView.class)
@PermitAll
public class ListView extends Div {

    @Autowired
    private ISysPostService postService;

    public ListView() {
        // 创建 ProTable
        ProTable<SysPost> proTable = new ProTable<>(SysPost.class)
                .setColumns(createColumns())
                .setHeaderTitle("岗位列表")
                .setRowKey(SysPost.Fields.postId);

        // 设置数据源
        FetchCallback<SysPost> fetchCallback = fetchParam -> {
            List<SysPost> resultList = postService.selectPostList(null);
            return new FetchResult<SysPost>()
                    .setTotal((long) resultList.size())
                    .setRecords(resultList);
        };
        proTable.setItems(fetchCallback);

        add(proTable);
    }

    /**
     * 创建表格列
     */
    private List<ProColumn<SysPost>> createColumns() {
        return List.of(
                new ProColumn<SysPost>()
                        .setTitle("岗位编号")
                        .setDataIndex(SysPost::getPostId),
                new ProColumn<SysPost>()
                        .setTitle("岗位编码")
                        .setDataIndex(SysPost::getPostCode),
                new ProColumn<SysPost>()
                        .setTitle("岗位名称")
                        .setDataIndex(SysPost::getPostName),
                new ProColumn<SysPost>()
                        .setTitle("状态")
                        .setDataIndex(SysPost::getStatus)
        );
    }
}
```


## 演示图

<table>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-9996b274886e8134066ccee096fde2089dd.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-66afe06885d34482862536e4f00c87c0475.png"/></td>
    </tr>    
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-f279ee4e419e9ba80a77fd898ebd8c9ac45.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-b56c891e29d1dfd0213b000339effd256db.png"/></td>
    </tr>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-26d4a0f56967f4c319d6e95cab9652bdbfe.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-125aed48a8214551cb2ce5aa5a1403d78e9.png"/></td>
    </tr>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-59bc1efe5d8f109e56305aa86192ff56bb0.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-6e081044a6f864c96df9a25aaa26516f7fc.png"/></td>
    </tr>
	<tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-ed2e67f41c8a56e0db1215645a0d9dd1e52.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-2788241f7893ac8fbfd2b84813f60451755.png"/></td>
    </tr>
	<tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-eda1770f6383e0001439b56c3392012213d.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-31c487d7419b16bc79de0d6a6a12789f048.png"/></td>
    </tr>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-31c487d7419b16bc79de0d6a6a12789f048.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-4d8cd86ba198f0263f90a0bd36c47b0317b.png"/></td>
    </tr>
	<tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-6d0ba703a00f8b02a0540931c9e67fe816c.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-376159966aa67e7e2fdd971bf68fb0a3375.png"/></td>
    </tr>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-77b186361c754bd9abc6beac7b2dd371858.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-800aba850793feb11e52720153a801cc2e5.png"/></td>
    </tr>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-8835cf289be21d9ed81974764670d78d120.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-31a968948be45abb0a30bd7b69fd9bee501.png"/></td>
    </tr>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-ed8b654a35b70d5b14281c7d5f086658e27.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-e7f3e329aa2052d32f64a372f25ad9f5df1.png"/></td>
    </tr>
</table>

## 参考
- [vaadin-antd: ant design 的vaadin实现](https://gitee.com/muyun2800/vaadin-antd)
- [vaadin-antd-procomponents: ant design procomponents 的vaadin实现](https://gitee.com/muyun2800/vaadin-antd-procomponents)
- [vaadin-antd-charts: ant design charts 的vaadin实现 ](https://gitee.com/muyun2800/vaadin-antd-charts)
- [ruoyi-vaadin: ruoyi的vaadin实现](https://gitee.com/muyun2800/ruoyi-vaadin)