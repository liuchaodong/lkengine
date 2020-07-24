package cn.linkey.orm.factory;


import cn.linkey.orm.dao.Rdb;
import cn.linkey.orm.dao.impl.RdbImpl;
import cn.linkey.orm.doc.Document;
import cn.linkey.orm.doc.Documents;
import cn.linkey.orm.doc.impl.DocumentImpl;
import cn.linkey.orm.doc.impl.DocumentsImpl;
import cn.linkey.orm.util.DateUtil;
import cn.linkey.orm.util.JdbcUtil;

import java.sql.Connection;
import java.util.HashMap;

/**
 * 本类为系统的容器类，所有对像应使用本类来进行创建和获取，而不要单独使用new来创建
 *
 * <p>本类主要功能为根据请求创建的类来判断容器中是否已经存在实例对像，<br>
 * 如果已经存在就直接返回对像实例 如果不存在则调用LinkeyObj去创建一个出来系统核心类进行缓存。
 * <p>而对于规则和用户自定义的类则不进行缓存 BeanCtx.init("admin",null,null);<br>
 * 这个需要在过虑器中进行初始化BeanCtx.close(); 本类为静态单例类。
 */
final public class BeanCtx {
	private static ThreadLocal<ThreadContext> context = new ThreadLocal<ThreadContext>(); // 线程全局对像,通过get // set方式访问*/


	/**
	 * 从线程变量中获得默认数据源的链接对像
	 * 如果线程链接对像存在则返回线程链接
	 * 如果不存在则创建一个新的链接对像并放入到线程变量中去
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception {
		//synchronized
		Connection conn = null;
		ThreadContext insThreadContext = BeanCtx.getContext();
		conn = insThreadContext.getConnection();
		// 首先从当前线程中拿链接，如果拿到了就直接返回否则就从数据库池中拿
		if (conn == null) {

			conn = JdbcUtil.getConnection();
			// 把拿到的conn设置到当前线程中
			BeanCtx.setConnection(conn);
		}
		return conn;
	}

	/**
	 * 从线程变量中获得默认数据源的链接对像
	 * 如果线程链接对像存在则返回线程链接
	 * 如果不存在则创建一个新的链接对像并放入到线程变量中去
	 * @param dbDriver
	 * @param dbUser
	 * @param dbPwd
	 * @param dbUrl
	 * @return
	 */
	public static Connection getConnection(String dbDriver, String dbUser, String dbPwd, String dbUrl) {
		Connection conn = null;
		ThreadContext insThreadContext = BeanCtx.getContext();
		conn = insThreadContext.getConnection();
		// 首先从当前线程中拿链接，如果拿到了就直接返回否则就从数据库池中拿
		if (conn == null) {

			conn = JdbcUtil.getConnection(dbDriver,dbUrl,dbUser,dbPwd);
			// 把拿到的conn设置到当前线程中
			BeanCtx.setConnection(conn);
		}
		return conn;
	}


	/**
	 * 获得线程级别的变量对像
	 * @return  返回一个ThreadContext对象
	 */
	public static ThreadContext getContext() {
		// 初始化线程对像
		ThreadContext insThreadContext = context.get();
		if (insThreadContext == null) {
			insThreadContext = new ThreadContext();
			context.set(insThreadContext);
		}
		return insThreadContext;
	}

	/**
	 * 设置线程级别的变量对像
	 *
	 * @param obj 传入一个ThreadContext对象
	 */
	public static void setContext(ThreadContext obj) {
		context.set(obj);
	}

	/**
	 * 设置全局的数据库链接对像
	 *
	 * @param conn 传入一个Connection对象
	 */
	public static void setConnection(Connection conn) {
		getContext().setConnection(conn);
	}

	/**
	 * 获得回滚标记
	 *
	 * @return true表示需要回滚,false表示不需要
	 */
	public static boolean isRollBack() {
		return getContext().isRollBack();
	}

	/**
	 * 设置回滚标记
	 *
	 * @param rollBack true表示需要回滚，false表示不需要
	 */
	public static void setRollback(boolean rollBack) {
		// log("D", "数据库链接被设置为需要回滚!");
		getContext().setRollback(rollBack);
	}

	/**
	 * 设置存盘时需要对文档的内容进行编码,默认就是编码的无需设置
	 */
	public static void setDocEncode() {
		BeanCtx.setCtxData("WF_NoEncode", "0");
	}

	/**
	 * 设置存盘时不对文档的内容进行编码
	 */
	public static void setDocNotEncode() {
		BeanCtx.setCtxData("WF_NoEncode", "1");
	}

	/**
	 * 获取当前文档是否编码的状状态
	 *
	 * @return 返回true表示文档存盘时需要编码 返回false表示文档存盘时不进行编码
	 */
	public static boolean getEnCodeStatus() {
		String encode = (String) BeanCtx.getCtxData("WF_NoEncode");
		if (encode != null && encode.equals("1")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取全局变量对像,返回Object对像
	 *
	 * @param key key
	 * @return Object对像
	 */
	public static Object getCtxData(String key) {
		return getContext().getCtxData(key);
	}

	/**
	 * 获取全局变量对像，返回字符串
	 *
	 * @param key key
	 * @return 字符串
	 */
	public static String getCtxDataStr(String key) {
		return getContext().getCtxDataStr(key);
	}

	/**
	 * 设置全局线程变量对像
	 *
	 * @param key key
	 * @param obj obj
	 */
	public static void setCtxData(String key, Object obj) {
		getContext().setCtxData(key, obj);
	}


	/**
	 * 获得Document对像专用方法
	 *
	 * @param tableName 数据库表名
	 * @return 返回一个Document对象
	 */
	public static Document getDocumentBean(String tableName) {
		return new DocumentImpl(tableName);
	}

	/**
	 * 获得文档对像
	 * @param conn 数据库链接对像
	 * @param tableName 数据库表
	 * @return Document对象
	 */
	public static Document getDocumentBean(Connection conn, String tableName) {
		return new DocumentImpl(tableName, conn);
	}

	/**
	 * 获取Documents对象
	 * @return
	 */
	public static Documents getDocumentsBean(){
		return new DocumentsImpl();
	}
	/**
	 *获取Rdb对象
	 * @return
	 */
	public static Rdb getRdbBean(){
		return new RdbImpl();
	}

	/**
	 * 根据类型和类名返回实例对像，不需要强制类型转换
	 * @param cls
	 * @param beanid
	 * @return
	 */
	private static <T> T getBean(Class<T> cls, String beanid) {
		HashMap<String, String> configMap = BeanConfig.getClassPath(beanid);
		String className = configMap.get("classPath");
		String singleton = configMap.get("singleton");
		T newobj = null;
		try {
			if (singleton.equals("0")) { // 多例模式
				Class<T> r = (Class<T>) Class.forName(className);
				newobj = (T) r.newInstance();
			} else {
				// singleton=1单例模式
				if (newobj == null) {
					// 说明池中还没有，创建一个新实例并放入其中
					Class<T> r = (Class<T>) Class.forName(className);
					newobj = (T) r.newInstance();
				}
			}
			return newobj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 销毁线程对像以及线程级别的数据库链接对像
	 */
	public static void close() {
		// 1.如果开启了事务要进行提交或回滚
		try {
			Connection conn = BeanCtx.getRdbBean().getConnection();
			if (conn != null && !conn.isClosed()) {
				if (BeanCtx.getRdbBean().getAutoCommit() == false) {
					if (BeanCtx.isRollBack()) {
						// 程序运行出错或者程序主动设置需要回滚，则所有数据回滚
						BeanCtx.getRdbBean().rollback();

						System.out.println(DateUtil.getNow() + "所有数据被成功回滚!");
					} else {
						// 程序运行没有出错，或者出错也不要求回滚的情况下
						conn.commit(); // 提交事务，不需回滚提交所有数据
					}
					BeanCtx.getRdbBean().setAutoCommit(true); // 恢复到非事务状态
				} else {
					if (BeanCtx.getRdbBean().getDbType().equals("ORACLE")) {
						conn.commit(); // 这里必须执行commit()因为oracle数据库必须主动commit()不然会产生死锁
					}
				}
			}

			// 2.关闭数据库链接

			// 关闭数据库链接,在线程关闭程序中有关闭功能

			// 清除线程变量
			ThreadContext insThreadContext = context.get();
			if (insThreadContext != null) {
				insThreadContext.close();
				insThreadContext = null;
			} else {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			}
			context.remove();
		} catch (Exception e) {
			System.out.println("BeanCtx关闭出错!");
		} finally {
			context.remove();
		}
	}


}
