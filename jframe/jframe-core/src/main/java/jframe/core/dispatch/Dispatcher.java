package jframe.core.dispatch;

import java.util.Collection;

import jframe.core.msg.Msg;

/**
 * 分发消息接口
 * 
 * @ThreadSafe
 * @author dzh
 * @date Jun 18, 2013 4:16:46 PM
 */
public interface Dispatcher {

	String getID();

	/**
	 * @Title start
	 * @Description 启动
	 * @return void
	 * @throws
	 */
	void start();

	/**
	 * @Title receive
	 * @Description 接收消息
	 * @param msg
	 * @return void
	 * @throws
	 */
	void receive(Msg<?> msg);

	/**
	 * @Title dispatch
	 * @Description 分发消息
	 * @param msg
	 * @return boolean
	 * @throws
	 */
	boolean dispatch(Msg<?> msg);

	void addDispatchSource(DispatchSource source);

	void removeDispatchSource(DispatchSource source);

	void addDispatchTarget(DispatchTarget target);

	void removeDispatchTarget(DispatchTarget target);

	Collection<DispatchSource> getDispatchSource();

	Collection<DispatchTarget> getDispatchTarget();

	/**
	 * @Title: close
	 * @Description 关闭、清理资源
	 * @return void
	 * @throws
	 */
	void close();

}
