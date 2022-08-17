## Hero-Helper

枭雄（一款页面网游）辅助。


## 游戏网址

- [手机ok网](http://wapok.cn/)

(PS: 以上是我目前已知能玩这款游戏的网址，如果你知道其它的网址，欢迎提交PR)


## 功能

- [x] 领取每日福利
    - [x] 开启今日修神
    - [x] 在线时间兑换
    - [x] 领取每日破碎梦晶石(151级)
    - [x] 领取每日古城传说图谱(151级)
    - [x] ...
- [x] 固定NPC
    - [x] 青蛇
    - [x] 老黄牛
    - [x] 宝库守卫
    - [x] 巨蛇
    - [x] 牵汗血宝马
    - [x] 潜能转资源(100:50)
    - [x] 潜能转资源(100:100)
    - [x] ...
- [x] 定时刷新NPC
    - [x] 柳树林
    - [x] 桃花阵
    - [x] 流花河
    - [x] 景阳岗树林
    - [x] 柴家庄红树林
    - [x] 东京菜园
    - [x] 绿竹林
    - [x] 幽暗池
    - [x] 摘星子
    - [x] 追风神盗
    - [x] 蜂王
    - [x] 巨大老龟
    - [x] 邪龙
    - [x] 巨大青蛇
    - [x] ...
- [x] 在线挂机


## 快速启动

1. 运行启动类 `com.itplh.hero.HeroHelperApplication`
2. 浏览器访问：http://localhost:8080


## 技术栈

### 开发环境

- 语言：OpenJDK 8

- IDE： IDEA (须安装lombok插件 )

### 后端

- 基础框架：spring boot 2.3.4.RELEASE
- HTML解析库：jsoup 1.15.2
- 代码简化：lombok 1.18.12
- 日志打印：logback 1.2.3

### 前端

- 基础框架：vue 3.2.37
- UI库：ant-design-vue 2.2.2
- HTTP请求库：axios 0.27.2
- 时间处理库：moment.js 2.29.1


## 业务流程

### POST /event/trigger

![POST /event/trigger flow chart](https://raw.githubusercontent.com/tanpenggood/hero-helper/main/images/EventController_eventTrigger.png)

### Listener doOnApplicationEvent

![Listener doOnApplicationEvent flow chart](https://raw.githubusercontent.com/tanpenggood/hero-helper/main/images/DefaultApplicationListenerHelper_doOnApplicationEvent.png)

### Service handle

![Service handle flow chart](https://raw.githubusercontent.com/tanpenggood/hero-helper/main/images/EventHandleServiceImpl_handle.png)


## 系统效果

### Event Trigger Tool

![Event Trigger Tool](https://raw.githubusercontent.com/tanpenggood/hero-helper/main/images/Page-EventTriggerTool.jpeg)

### Region User Manager

![Region User Manager](https://raw.githubusercontent.com/tanpenggood/hero-helper/main/images/Page-UserManager.jpeg)


## Visitors

![](http://profile-counter.glitch.me/tanpenggood/count.svg)

## Contributors

<a href="https://github.com/tanpenggood/hero-helper/contributors">
  <img src="https://contributors-img.web.app/image?repo=tanpenggood/hero-helper" />
</a>