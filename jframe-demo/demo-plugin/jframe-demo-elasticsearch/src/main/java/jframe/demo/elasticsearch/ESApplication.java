/**
 * 
 */
package jframe.demo.elasticsearch;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import jframe.demo.elasticsearch.weike.WeikePath;

/**
 * @author dzh
 * @date Jul 31, 2016 10:30:20 PM
 * @since 1.0
 */
public class ESApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(Test.class);
        classes.add(WeikePath.class);
        return classes;
    }

}
