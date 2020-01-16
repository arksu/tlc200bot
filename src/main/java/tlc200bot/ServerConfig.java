package tlc200bot;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class ServerConfig
{
	private static String WORK_DIR = "./";

	public static String DB_USER;
	public static String DB_PASSWORD;
	public static String DB_NAME;

	public static String INTERNAL_URL;
	public static String EXTERNAL_URL;
	public static String TOKEN;
	public static String PATH;

	public static void loadConfig()
	{
		File configFile = new File(WORK_DIR + "config/server.conf");

		Config conf = ConfigFactory
				.parseFile(configFile)
				.withFallback(ConfigFactory.load("server.defaults.conf"));

		DB_USER = conf.getString("db.user");
		DB_PASSWORD = conf.getString("db.password");
		DB_NAME = conf.getString("db.name");

		INTERNAL_URL = conf.getString("int_url");
		EXTERNAL_URL = conf.getString("ext_url");
		TOKEN = conf.getString("token");
		PATH = conf.getString("path");
	}
}
