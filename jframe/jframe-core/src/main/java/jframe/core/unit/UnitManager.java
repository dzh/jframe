/**
 * 
 */
package jframe.core.unit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.Config;
import jframe.core.util.MathUtil;
import jframe.core.util.PropsConf;

/**
 * <p>
 * UnitManager
 * </p>
 * 
 * @author dzh
 * @date Sep 24, 2013 11:07:49 AM
 * @since 1.0
 */
public class UnitManager {

    private static final Logger LOG = LoggerFactory.getLogger(UnitManager.class);

    private List<Unit> _units;
    private Config _conf;

    public UnitManager(Config conf) {
        _units = new LinkedList<Unit>();
        _conf = conf;
    }

    public static final UnitManager createManager(Config conf) {
        UnitManager um = new UnitManager(conf);
        return um;
    }

    /**
     * register unit, to be unregistered if u exist
     * 
     * @param u
     * @return
     * @throws UnitException
     */
    public Unit regUnit(Unit u) throws UnitException {
        if (u == null)
            throw new UnitException("m->regUnit Unit is null");

        synchronized (_units) {
            if (_units.contains(u)) {
                // delete old unit
                unregUnit(_units.get(_units.indexOf(u)));
            }
            u.init(_conf.getFrame());
            // calculate min and unused natural number
            List<Integer> ids = new ArrayList<Integer>(_units.size());
            for (Unit unit : _units) {
                ids.add(unit.getID());
            }
            u.setID(MathUtil.calcMinNum(ids));

            u.start();
            _units.add(u);
        }

        LOG.info("m->regUnit u->{}", u);
        return u;
    }

    /**
     * unregister unit
     * 
     * @param u
     * @return
     * @throws UnitException
     */
    public Unit unregUnit(Unit u) throws UnitException {
        if (u == null)
            throw new UnitException("Unit is null when invoking unregUnit()");

        u.stop();
        synchronized (_units) {
            _units.remove(u);
        }

        LOG.info("m->unregUnit u->{}", u);
        return u;
    }

    public List<Unit> getAllUnits() {
        synchronized (_units) {
            return Collections.unmodifiableList(_units);
        }
    }

    public void dispose() {
        synchronized (_units) {
            for (Unit u : _units) {
                try {
                    unregUnit(u);
                } catch (UnitException e) {
                    LOG.warn(e.getMessage(), e.fillInStackTrace());
                }
            }
        }
        _units = null;
        _conf = null;
    }

    /**
     * <p>
     * Initialize Manager and Register Units
     * </p>
     * 
     * @throws UnitException
     */
    public void start() throws UnitException {
        String funit = _conf.getConfig("file.unit", _conf.getConfig(Config.APP_CONF + "/unit.properties"));
        if (new File(funit).exists()) {
            loadUnit(funit);
        } else {
            loadUnit(null);
        }
    }

    /**
     * load unit from unit.properties
     * 
     * @param file
     * @throws UnitException
     */
    private void loadUnit(String file) throws UnitException {
        if (file == null) {
            regUnit(new FrameUnit());
            regUnit(new PluginUnit());
            return;
        }

        LOG.info("m->loadUnit f->{}", file);
        try {
            PropsConf props = new PropsConf();
            props.init(file);

            String[] units = props.getGroupIds();
            for (String id : units) {
                String clazz = props.getConf(id, "class");
                Unit unit = (Unit) Class.forName(clazz, true, Thread.currentThread().getContextClassLoader())
                        .newInstance();
                unit.setName(props.getConf(id, "name", unit.getClass().getSimpleName()));
                regUnit(unit);
            }
        } catch (Exception e) {
            throw new UnitException("PropsConf.init error unit->" + file, e.getCause());
        }
    }

}
