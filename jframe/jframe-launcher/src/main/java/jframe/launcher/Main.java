/**
 * 
 */
package jframe.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jframe.core.conf.Config;
import jframe.core.conf.ConfigConstants;
import jframe.core.conf.DefConfig;
import jframe.core.conf.VarHandler;
import jframe.core.util.ConfigUtil;
import jframe.core.util.Program;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

/**
 * <p>
 * daemon运行
 * <li>加载config.properties</li>
 * <li>判断是否daemon执行</li>
 * <li>写PID到daemon.pid</li>
 * <li>配置子进程(app.home)，调用launcher启动子进程，默認launcher是FrameMain</li>
 * <li>监听子进程，若异常停止则重启</li>
 * <li>子进程正常关闭时，自己也关闭</li>
 * </p>
 * <p>
 * normal运行
 * <li>加载config.properties</li>
 * <li>判断是否daemon执行</li>
 * <li>配置子进程(app.home)，调用launcher启动子进程</li>
 * <li>正常终止自己</li>
 * </p>
 * <p>
 * 父子进程关系
 * <li>父进程拷贝所有系统属性给子进程</li>
 * </p>
 * 
 * @author dzh
 * @date Oct 10, 2013 4:28:16 PM
 * @since 1.0
 */
public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LOG.info("Main is Starting!");
		String configFile = System.getProperty(ConfigConstants.FILE_CONFIG);
		if (configFile == null || "".equals(configFile)) {
			configFile = System.getProperty(ConfigConstants.APP_HOME)
					+ File.separator + "conf" + File.separator
					+ ConfigConstants.FILE_CONFIG;
		}

		File confFile = new File(configFile);
		if (!confFile.exists()) {
			LOG.error("Not found config.properties");
			exitSystem(-1);
		}

		final Config config = ConfigUtil.genNewConfig(configFile);
		LOG.info("Loading config.properties successfully!");

		String daemon = config.getConfig(ConfigConstants.LAUNCH_MODE);
		if (ConfigConstants.LAUNCH_MODE_DAEMON.equalsIgnoreCase(daemon)) {
			launchDaemon(config);
		} else {
			launchNormal(config);
		}

		LOG.info("Daemon process stopped!");
		exitSystem(0);
	}

	private static void exitSystem(int status) {
		stopLog();
		System.exit(status);
	}

	/**
	 * @param conf
	 */
	private static void launchNormal(Config config) {
		LOG.info("Launching normal mode!");
		launch(config);
	}

	private static Process launch(Config config) {
		// TODO class is not found
		String launcher = config.getConfig(ConfigConstants.LAUNCHER);
		if (launcher == null) {
			launcher = FrameMain.class.getName(); //
		}
		List<String> list = loadVmargs(config
				.getConfig(ConfigConstants.FILE_VMARGS));
		VarHandler vh = new VarHandler(config);
		for (int i = 0; i < list.size(); i++) {
			String arg = list.get(i);
			if (vh.hasVar(arg)) {
				list.set(i, vh.replace(arg));
			}
		}

		list.add(0, "-Dapp.home=" + config.getConfig(ConfigConstants.APP_HOME));
		list.add(0, "java");
		list.add(launcher);
		ProcessBuilder pb = new ProcessBuilder();
		pb.command(list);

		Process p = null;
		try {
			p = pb.start();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return p;
	}

	/**
	 * @param string
	 */
	private static List<String> loadVmargs(String file) {
		File f = new File(file); // vmargs file
		if (!f.exists()) {
			LOG.error("Not found vmargs file:" + file);
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
				if (line.startsWith("#") || line.equals(""))
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

	/**
	 * @param conf
	 */
	private static void launchDaemon(Config config) {
		LOG.info("Launching daemon mode!");
		LOG.info("Write pid file: " + config.getConfig(DefConfig.PID_DAEMON));

		try {
			Program.writePID(Program.getPID(),
					config.getConfig(DefConfig.PID_DAEMON));
		} catch (IOException e) {
			LOG.error(e.getMessage());
			exitSystem(-1);
		}

		final String pid_daemon = config.getConfig(ConfigConstants.PID_DAEMON);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					new File(pid_daemon).deleteOnExit();
				} catch (Exception e) {
					LOG.error("Shutdown Error:" + e.getMessage());
				}
			}
		});

		int exit = 0;
		do {
			Process subp = launch(config);//
			LOG.info("Startup application process successfully!");
			try {
				exit = subp.waitFor();
			} catch (InterruptedException e) {
				LOG.warn(e.getLocalizedMessage()); // TODO
			}
		} while (exit != 0 && exit != 143);
	}

	private static final void stopLog() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory
				.getILoggerFactory();
		loggerContext.stop();
	}
}
