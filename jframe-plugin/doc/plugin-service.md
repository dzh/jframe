插件和服务
==========================
插件和插件服务说明

### 插件服务目录
* jframe-datasource
- jframe.datasource.DataSourceService
	- @Service(clazz = "jframe.datasource.druid.DruidServiceImpl", id = "jframe.service.datasource")

* jframe-mybatis
-jframe.mybatis.MybatisService
	- @Service(clazz = "jframe.mybatis.MybatisServiceImpl", id = "jframe.service.mybatis")

* jframe-pushy
- jframe.pushy.PushyService
	- @Service(clazz = "jframe.pushy.impl.PushyServiceImpl", id = "jframe.service.pushy")

* jframe-getui
- jframe.getui.GetuiService
	- @Service(clazz = "jframe.getui.andriod.GetuiServiceImpl", id = "jframe.service.getui")
	
* jframe-memcached-client
- jframe.memcached.client.MemcachedService
	- @Service(clazz = "jframe.memcached.client.MemcachedServiceImpl", id = "jframe.service.memcached.client")
	
* jframe-jedis
- jframe.jedis.service.JedisService
	- @Service(clazz = "jframe.jedis.service.JedisServiceImpl", id = "jframe.service.jedis")

