/**
 * 
 */
package jframe.core.unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jframe.core.conf.Config;
import jframe.core.util.MathUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOG = LoggerFactory
			.getLogger(UnitManager.class);

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
	 * register unit
	 * 
	 * @param u
	 * @return
	 * @throws UnitException
	 */
	public Unit regUnit(Unit u) throws UnitException {
		if (u == null)
			throw new UnitException("Unit is null when invoking regUnit()");

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

		synchronized (_units) {
			u.stop();
			_units.remove(u);
		}
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
					LOG.warn(e.getLocalizedMessage()); // TODO
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
	 * TODO
	 * 
	 * @throws UnitException
	 */
	public void start() throws UnitException {
		// 加载unit
		// 1.从units.xml加载unit
		regUnit(new FrameUnit());
		regUnit(new PluginUnit());
	}

}
