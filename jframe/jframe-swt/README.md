JFrame SWT Plugin
===========================
This is a UI plugin based on JFrame and SWT library,it provides a basic UI function.
You can use it to custom your UI plugin.

# SWT资源名称
org.eclipse.swt.carbon.macosx
org.eclipse.swt.cocoa.macosx
org.eclipse.swt.cocoa.macosx.x86_64
org.eclipse.swt.gtk.aix.ppc
org.eclipse.swt.gtk.aix.ppc64
org.eclipse.swt.gtk.hpux.ia64
org.eclipse.swt.gtk.hpux.ia64_32
org.eclipse.swt.gtk.linux.ppc
org.eclipse.swt.gtk.linux.ppc64
org.eclipse.swt.gtk.linux.s390
org.eclipse.swt.gtk.linux.s390x
org.eclipse.swt.gtk.linux.x86
org.eclipse.swt.gtk.linux.x86_64
org.eclipse.swt.gtk.solaris.sparc
org.eclipse.swt.gtk.solaris.x86
org.eclipse.swt.motif.linux.x86
org.eclipse.swt.win32.win32.x86
org.eclipse.swt.win32.win32.x86_64

# 添加jface支持
		<dependency>
			<groupId>org.eclipse.jface</groupId>
			<artifactId>org.eclipse.jface</artifactId>
			<version>3.8.0.v20120521-2329</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.eclipse.core</groupId>
			<artifactId>org.eclipse.core.commands</artifactId>
			<version>3.6.0</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.eclipse.equinox</groupId>
			<artifactId>org.eclipse.equinox.common</artifactId>
			<version>3.6.0.v20100503</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.eclipse.osgi</groupId>
			<artifactId>org.eclipse.osgi</artifactId>
			<version>3.8.0.v20120529-1548</version>
			<scope>provided</scope>
		</dependency>
# 