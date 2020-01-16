package tlc200bot;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jpark.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tlc200bot.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Database
{
	private static final Logger _log = LoggerFactory.getLogger(Database.class.getName());

	private static DataSource source;

	private static EntityManager _em = new EntityManager();

	public static void start()
	{
		_em.addEntityClass(User.class);

		_em.setConnectionFactory(Database::getConnection);
		//**************************************************

		HikariConfig config = new HikariConfig();

		config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");

		config.setMinimumIdle(10);
		config.setMaximumPoolSize(20);

		config.addDataSourceProperty("user", ServerConfig.DB_USER);
		config.addDataSourceProperty("password", ServerConfig.DB_PASSWORD);
		config.addDataSourceProperty("databaseName", ServerConfig.DB_NAME);
		config.addDataSourceProperty("loginTimeout", 2);

		config.setLeakDetectionThreshold(5000);
		config.setConnectionTimeout(30000);

		source = new HikariDataSource(config);

		try
		{
			Connection connection = getConnection();
			_em.deploy();
			connection.close();
		}
		catch (SQLException e)
		{
			_log.error("connect error", e);
		}
	}

	public static EntityManager em()
	{
		return _em;
	}

	/**
	 * получить коннект до базы
	 */
	public static Connection getConnection()
	{
		try
		{
			return source.getConnection();
		}
		catch (SQLException e)
		{
			_log.error("get connection error: " + e.getMessage(), e);
		}
		throw new RuntimeException("no available connection");
	}
}
