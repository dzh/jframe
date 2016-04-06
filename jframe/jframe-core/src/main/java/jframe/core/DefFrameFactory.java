/**
 * 
 */
package jframe.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.Config;
import jframe.core.conf.ConfigConstants;

/**
 * @author dzh
 * @date Jun 7, 2013 1:43:33 PM
 */
public class DefFrameFactory implements FrameFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DefFrameFactory.class);

    /*
     * (non-Javadoc)
     * 
     * @see jframe.core.FrameFactory#createFrame(jframe.core.conf.Config)
     */
    @SuppressWarnings("unchecked")
    public Frame createFrame(Config config) {
        try {
            Class<Frame> frame = (Class<Frame>) Class.forName(config.getConfig(ConfigConstants.APP_FRAME), true,
                    Thread.currentThread().getContextClassLoader());
            Frame f = frame.newInstance();
            f.init(config);
            config.setFrame(f);
            return f;
        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }

    public void handleException(Throwable exception) {
        LOG.error(exception.getLocalizedMessage());
    }
}