/**
 *
 */
package jframe.core.dispatch;

import java.util.ArrayList;
import java.util.List;

import jframe.core.conf.Config;

/**
 * dispatch管理管理工厂
 *
 * @author dzh
 * @date Jun 20, 2013 9:47:12 AM
 */
public class DefDispatchFactory implements DispatchFactory {

    private final Object _lock = new Object();

    private List<Dispatcher> _dList = new ArrayList<Dispatcher>(2);

    private DefDispatchFactory() {

    }

    public static DispatchFactory newInstance() {
        return new DefDispatchFactory();
    }

    public Dispatcher findDispatcher(String dispatcherID) {
        List<Dispatcher> list = _dList;
        Dispatcher dl = null;
        synchronized (_lock) {
            for (Dispatcher d : list) {
                if (d.getID().equals(dispatcherID)) {
                    dl = d;
                    break;
                }
            }
        }
        return dl;
    }

    public Dispatcher createDispatcher(String dispatcherID, Config config) {
        Dispatcher d = DefDispatcher.newDispatcher(dispatcherID, config);
        d.start();
        synchronized (_lock) {
            _dList.add(d);
        }
        return d;
    }

    /**
     * if dispatcherID==Null, close all delegates
     */
    public void removeDispatcher(String dispatcherID) {
        List<Dispatcher> list = _dList;
        if (dispatcherID == null) {
            synchronized (_lock) {
                for (Dispatcher d : list) {
                    d.close();
                }
            }
            list.clear();
        } else {
            synchronized (_lock) {
                Dispatcher d = findDispatcher(dispatcherID);
                if (d != null) {
                    d.close();
                    list.remove(d);
                }
            }
        }
    }

}
