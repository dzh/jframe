jframe watch plugin
==================
目录监听插件

# config.ini
*用空格分隔属性*
watch.path = ${app.home}/plugin ${app.home}/update #监测目录
watch.file = jar #监测文件，用正则表示,未支持 TODO
watch.file.suffix = jar #监测文件后缀
# feature
* 监听${app.home}/conf/, 对config.properties修改做监听
* 监听${app.home}/plugin/, 发送插件改动的通知
