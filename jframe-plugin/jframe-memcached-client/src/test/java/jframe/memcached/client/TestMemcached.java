package jframe.memcached.client;


public class TestMemcached {

	public void testConf() {
		String servers = "s1,s2 s3";
		String[] str = servers.split("[,\\s+]");
		System.out.println(str[0] + " " + str[1] + " " + str.length);
	}

}
