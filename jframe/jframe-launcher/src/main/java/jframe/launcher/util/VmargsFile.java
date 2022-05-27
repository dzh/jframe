/**
 * 
 */
package jframe.launcher.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Feb 16, 2016 12:34:31 PM
 * @since 1.0
 */
public class VmargsFile {

    Logger LOG = LoggerFactory.getLogger(VmargsFile.class);

    /**
     * @param file
     */
    public List<String> loadVmargs(String file) {
        File f = new File(file); // vmargs file
        if (!f.exists()) {
            LOG.error("Not found vmargs file {}", file);
            return Collections.emptyList();
        }
        List<String> vmargs = new LinkedList<String>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            String[] args = null;
            while (true) {
                try {
                    line = br.readLine(); // ignore single line error
                } catch (IOException e) {
                    LOG.warn(e.getMessage());
                    continue;
                }
                if (line == null)
                    break;
                if (line.trim().startsWith("#") || line.equals(""))
                    continue;
                args = line.split("\\s");
                for (String a : args) {
                    vmargs.add(a);
                }
            }
        } catch (FileNotFoundException e) {
            LOG.warn(e.getLocalizedMessage());
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    LOG.warn(e.getLocalizedMessage());
                }
        }
        return vmargs;
    }

}
