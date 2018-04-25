package com.jettyweb.db;

import com.jettyweb.conf.ResUtils;
import com.jettyweb.exception.SystemException;
import com.jettyweb.log.Log;
import com.jettyweb.util.Assert;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负责创建SqlSession，但不负责SqlSession的事务等操作。<BR>
 * reload用于重新加载。<BR>
 * 在无法确认是只读的情况下，就使用写库
 */
public class SqlSessionFactory {

	static Log logger = Log.get(SqlSessionFactory.class);

	private static Configuration configuration;
	private static Map<String, SqlSessionFactory> factoryMap = new ConcurrentHashMap<>();

	private WeightedDataSourceRoute read;
	private WeightedDataSourceRoute write;

	private String db;

	private SqlSessionFactory(String dbName) {
		this.db = dbName;
	}

	public Map<String, Map<String, Integer>> status() {
		Set<DataSource> set = new HashSet<>();
		set.addAll(this.read.allDataSource());
		set.addAll(this.write.allDataSource());
		Map<String, Map<String, Integer>> statusMap = new HashMap<>();
		for (DataSource datasource : set) {
			if (!BasicDataSource.class.isInstance(datasource)) {
				Log.get(this.getClass(), 25345).info("ds.class({}) is not instance form BasicDataSource",
						datasource.getClass().getName());
				continue;
			}
			@SuppressWarnings("resource")
			BasicDataSource ds = (BasicDataSource) datasource;
			Map<String, Integer> map = new HashMap<>();
			map.put("active", ds.getNumActive());
			map.put("idle", ds.getNumIdle());
			map.put("minIdle", ds.getMinIdle());
			map.put("maxIdle", ds.getMaxIdle());
			map.put("maxTotal", ds.getMaxTotal());
			statusMap.put(ds.toString(), map);
		}
		return statusMap;
	}

	public static SqlSessionFactory get(String dbName) {
		try {
			Assert.hasText(dbName, "db name can not be empty");
			dbName = dbName.trim();
			SqlSessionFactory factory = factoryMap.get(dbName);
			if (factory != null) {
				return factory;
			}
			synchronized (SqlSessionFactory.class) {
				factory = factoryMap.get(dbName);
				if (factory != null) {
					return factory;
				}
				factory = new SqlSessionFactory(dbName);
				factory.init();
				factory.parseDatasource();
				factoryMap.put(dbName, factory);
			}
			return factory;
		} catch (Exception e) {
			Log.printStack(e);
			SystemException.throwException(100234325, "create factory failed");
			return null;
		}
	}

	void destroy() {

	}

	public static void reload(String dbName) throws Exception {
		Assert.hasText(dbName, "db name can not be empty");
		dbName = dbName.trim();
		SqlSessionFactory factory = factoryMap.get(dbName);
		if (factory == null) {
			return;
		}
		factory = new SqlSessionFactory(dbName);
		factory.init();
		SqlSessionFactory old = factoryMap.put(dbName, factory);
		old.destroy();
	}

	private void parseDatasource() throws Exception {
		if (this.write != null || this.read != null) {
			Log.get(this.getClass(), 34534543).info("{} has init datasource", this.db);
			return;
		}
		Map<DBType, WeightedDataSourceRoute> map = DBFactory.create(this.db);
		this.write = map.get(DBType.WRITE);
		this.read = map.get(DBType.READONLY);
	}

	public SqlSession getSqlSession(DBType type) {
		Connection conn = getConnection(type);

		Transaction transaction = new JdbcTransaction(conn);
		SimpleExecutor excutor = new SimpleExecutor(configuration, transaction);
		return new DefaultSqlSession(configuration, excutor);
	}

	private Connection getConnection(DBType type) {
		if (!type.isWritable()) {
			try {
				return this.read.datasource().getConnection();
			} catch (SQLException e) {
				SystemException.throwException(100001, "获取" + db + "读连接失败", e);
			}
		}
		try {
			return this.write.datasource().getConnection();
		} catch (SQLException e) {
			SystemException.throwException(100001, "获取" + db + "写连接失败", e);
		}
		return null;
	}

	void init() throws Exception {
		configuration = new Configuration();
		Map<String, InputStream> sqls = ResUtils.dbResource(db).sqlXmls();
		Set<Map.Entry<String, InputStream>> entries = sqls.entrySet();
		for (Map.Entry<String, InputStream> entry : entries) {
			XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(entry.getValue(), configuration, entry.getKey(),
					configuration.getSqlFragments());
			xmlMapperBuilder.parse();
		}
	}

}
