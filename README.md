jframe
================
A Common Plugin Framework implemented by Java. Without complex configuration, you can use it now.

## Why do I develop it
* I need a hot deployment system, which support remote update, daemon process, easy configuration, mainly for message processing and ensure that the message is not lost. 
* Osgi is too complex, fully dynamic characteristics is not what I needed most. 
* I think a framework composed by plug-in, the communication between the plug-in via the message is a good idea.
* So I decided to write a lightweight general framework based on dynamic plugins which communicate via the message.

## Quick Start
* `git clone git@github.com:dzh/jframe.git` On my pc, the repo is at ~/git/jframe
* `import jframe into eclipse`, then "Run as" -> "Maven install" to build on jframe project.
* `cd ~/git/jframe/jframe/jframe-release/jframe`, and then `ls temp`
<p>
if there are any *.pid files in temp folder,delete them first
</p>
* `bin/jframe.sh start`, maybe you need `chmod +x bin/*.sh`. (If a windows user, use startup.bat)
<p>
**Note**:
Before to  start, modify `vmargs` in config.properties.If running on linux, set `vmargs = ${app.home}/conf/vmargs`,
if on windows ,set `vmargs = ${app.home}/conf/vmargs-win`.
The default will start three plug-ins:jframe-example-pluin, jframe-swt and jframe-watch.
</p>
* `jps -l`, if output similar content, then jframe start successfully.
<pre>
5516 jframe.launcher.Main // daemon
5517 jframe.launcher.Main // app
</pre>
You can also see log/*.log, or daemon.pid and app.pid generated in temp folder
* `bin/jframe.sh stop` (shutdown.bat), to stop jframe.

## About Jframe Manual
* Jframe's manual is jframe\_manual\_zh\_CN.org in /jframe/doc/ directory.I edit it using Emacs's Org plug-in, and export a html version jframe\_manual\_zh\_CN.html.
* Online address [jframe\_manual\_zh\_CN](https://github.com/dzh/jframe/blob/master/doc/jframe_manual_zh_CN.org)

## Question Contact
<pre>
jframe-dev@googlegroups.com  (u need join first)
dzh_11@qq.com                (personal)
</pre>
