Jframe Plugin Management
===============================
Provide installed plugins' information and Register/Unregister plugin interface.

### jpm protocol
C:	get
S:	pluginID pluginName pluginClazz jarPath
	....
C: 	[start|stop|restart pluginID] or [install|uninstall jarPath]
S: 	fnh success message|flr	failure message				#after working...
C:	exit	#exit connection


### config.properties



### jpm commands
* get
* plugin's command 
start
stop
restart
install
uninstall
* fnh
* flr
* exit


  