Clear File Plugin
=====================
清理文件的插件，用于测试环境

### 配置config.properties
- clrfile.dirs 清理目标目录，目录间用空格分隔
clrfile.dirs = /home/dzh/temp home/dzh/tmp
- clrfile.expire[digit] 文件的过期时间,单位分钟
clrfile.expire = 720 默认过期时间
clrfile.expire0 = 60 /home/dzh/temp目录里文件的过期时间
clrfile.expire1 = 120 /home/dzh/tmp 目录里文件的过期时间


