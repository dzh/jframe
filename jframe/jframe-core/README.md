JFrame Core
===============================================================
### Required JDK
* Run on jdk5 or later

### Plug-in Configuration File (plugin.properties)
* Property introduce
	* Plugin-ID, plugin Identifier which system define, it's a integer
	* Plugin-Name, plugin name which developer define with annotation, it's a ascii string
	* Plugin-Class, plugin class definition
	* Plugin-Lib, plugin dependency jar,
		* if defined ,program will load defined lib, else load All libs under lib folder
	* Plugin-Dll, plugin dynamic linked lib,it's win's dll or unix's so.
		* if defined ,program will load defined dll/so,else load all dll/so under dll folder
* Property is case insensitive

### Plug-in Loading Process
* PluginUnit
	* if exist -clean argument
		* delete all cache and reload plug-in 
		* copy configuration files and dependency jars to **cache** directory, and add urls to PluginClassLoader
	* load configuration files and dll/so from cache directory
	* load plug-ins under the **plugin** directory
### Updater Working Process
* Updater Plug-in
	* download installer zip file
	* execute installer script ()
		* stop plug-in -> copy to **plugin** directory and cover the origin plugin -> start plug-in
		* Stop plug-in -> copy to **plugin** directory and cover the origin plugin -> restart frame 
### Plug-in Cache Directory
* Plug-in -> hash code
	* lib/
	* dll/
	* META-INF/	
		
	
