/**
 * 
 */
package jframe.core.msg;

/**
 * @author dzh
 * @date Jan 7, 2014 10:03:53 PM
 * @since 1.0
 */
public class PoolMsg<V> implements Msg<V> {

	private Msg<V> _msg;

	private PoolMsg(Msg<V> msg) {
		this._msg = msg;
	}

	public static final <V> Msg<V> wrap(Msg<V> msg) {

		PoolMsg<V> pm = new PoolMsg<V>(msg);
		return pm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.msg.Msg#getType()
	 */
	public int getType() {
		return _msg.getType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.msg.Msg#setType(int)
	 */
	public Msg<V> setType(int type) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.msg.Msg#setMeta(java.lang.String, java.lang.String)
	 */
	public Msg<V> setMeta(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.msg.Msg#getMeta(java.lang.String)
	 */
	public String getMeta(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.msg.Msg#setValue(java.lang.Object)
	 */
	public Msg<V> setValue(V msg) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.msg.Msg#getValue()
	 */
	public V getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.msg.Msg#accept(jframe.core.msg.MsgVisitor)
	 */
	public void accept(MsgVisitor<V> visitor) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jframe.core.msg.Msg#clear()
	 */
	public void clear() {
		// TODO Auto-generated method stub

	}

}
