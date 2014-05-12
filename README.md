jframe
======
A Common Plugin Framework implemented by Java. Without complex configuration, you can use it now.

## Why do I develop it
* I need a hot deployment system, which support remote update, daemon process, easy configuration, mainly for message processing and ensure that the message is not lost. 
* Osgi is too complex, fully dynamic characteristics is not what I needed most. 
* I think a framework composed by plug-in, the communication between the plug-in via the message is a good idea.
* So I decided to write a lightweight general framework based on dynamic plugins which communicate via the message.

## About Jframe Manual
* Jframe's manual is jframe_manual_zh_CN.org in /jframe/doc/ directory.I edit it using Emacs's Org plug-in, and export a html version jframe_manual_zh_CN.html.
* Online address [jframe_manual_zh_CN](https://github.com/dzh/jframe/blob/master/doc/jframe_manual_zh_CN.org)

## Jframe Features
* v1.0.0
   * Run on jdk1.5 or higher
   * Use slf4j+logback as logging framework
   * Supoort daemon or normal launcher mode
   * Framework can be configured and can dynamically obtain configuration modification notification
   * Hot deployment plug-in system, and can control plug-in start/stop order
   * Communicate with each other via asynchronous message between plug-ins
   



