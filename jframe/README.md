jframe
===================================
Java插件框架

消息分发的效率低，
订阅的效率高，把收到的消息只給感興趣的（編譯時、運行時綁定的問題），訂閱都動態性如何解決

## 设计目标
- 实现通用的启动框架, 适用于服务端和桌面java程序
- 通过插件实现组件化编程, 有利于开发维护和部署升级, 支持热部署
- 内建消息通讯, 实现服务化, 有助于大规模团队开发

## 特性列表
### [通用的程序启动器](jframe-launcher/README.md)
- 构建在jframe-launcher上的插件工程一般结构

```
    - jframe project
        - bin                   jframe.sh or other executable scripts
        - conf                  configuration files
            - config.properties core configuration
            - vmargs            jvm arguments               
        - lib                   common jar libraries
            - *.jar
        - log                   log directory
        - plugin                plug-ins
            - *-plugin.jar      
        - tmp                   temporary or runtime files
            - .cache            plug-in caches
            - *.pid             daemon|app.pid
        - update                directory for plug-ins to be updated
```
- config.properties配置说明

## 工程
### jframe-core
* 设计原则
	* 异步运行、同步启停
	* 最小内核、资源可控（微内核+接口）
	* 接口統一
	* 插件间基于消息通讯
* jframe生命周期、插件资源释放
	* 
* 资源管理
	* MSG的持久化问题
* 插件化Plugin
	* 热部署
	* 插件间通讯，共享内存、消息分发、
	* 插件启停的对外控制接口
	* 插件生命周期事件
	* MANIFEST.MF插件配置文件
	* 插件元信息的管理
* 配置文件
	* 动态修改、即时生效 （这也是一个插件）
* Deamon守护进程
* 动态库加载
	* 能否无需重启,即时加载生效
* jframe分布式体系(核心+插件)
	* 协作模式：主从、消息路由、p2p（？）、
	* 状态同步
	* 消息订阅、共享
* jframe更新包设计
	* 框架起停的API，暴露給Plugin，基於異步的信號量
### jframe-swt
* 和框架核心、其他插件的通讯
* swt界面
### jframe-io
* nio
* aio
### jframe-conf
* 动态生效
### 示例
* 共享
* 分发

## TODO
### 待开发
- signal基于frame，msg基于plugin
- 重写插件支持2.0.0
- jframe监控
### 优化
- 优化守护进程占用的内存
- 使用消息池，降低临时对象造成的频繁gc
Fixed Bugs
- config.properties嵌套变量，没有处理
- 启动时，同名插件加载版本号最大的
- 修正插件复制lib、dll到缓存目录时的路径错误（只windows）

