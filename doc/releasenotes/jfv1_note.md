Jframe v1.x Release Notes
===================================

## Jframe v1 Feature
* v1.0.0
   * Run on jdk1.5 or higher
   * Use slf4j+logback as logging framework
   * Supoort daemon or normal launcher mode
   * Framework can be configured and can dynamically obtain configuration modification notification
   * Hot deployment plug-in system, and can control plug-in start/stop order
   * Communicate with each other via asynchronous message between plug-ins
* v1.1.0
  * Add plugin service feature, include annotation: `Service, InjectService, Start and Stop`
  * Add injecting feaure, include annotation: `Injector, InjectPlugin, InjectService`. For example, `InjectPlugin` which injecting self-plugin instance into classes of the plugin
  * New options in the plugin.properties, `Plugin-Service, Import-Plugin, Import-class, Export-class`
  * New plugin
  		* jframe-mybatis
    	* jframe-datasource
* v1.2.0
	* New plugin
		* jframe-yunpian
		* jframe-qiniu
		* jframe-mongodb-client
	* Add jframe-pay application(pay gateway), see [jframe\_pay\_manual\_zh\_CN](https://github.com/dzh/jframe/tree/master/jframe-pay/doc/jframe_pay_manual_zh_CN.org)
* v1.2.1 deving
	* TODO Fix hot deployment and support service/fragment feature
	* TODO Rewrite bin/jframe.[bat|sh] 
	* TODO Add plugin fragment feature
	* TODO Add stopping policy, such as save/restore msg when jframe begin stopping
	* TODO ActivemqDispatcher improvement
		* Automatic reconnection(static:(tcp://host1:61616,tcp://host2:61616)
		* Support Publish/Subscribe and Product/Consume
	* TODO Add RabbitmqDispatcher
	* Improvement
	* New Feature
		* jframe-launcher to support Launcher api
	* New Plugin
		* TODO jframe-activemq
		* TODO jframe-activemq-client
		* TODO jframe-http
		* TODO jframe-zk
		* TODO jframe-watch
		* jframe-rongyun
		* jframe-umeng
* v1.3.0 
	* service monitor

