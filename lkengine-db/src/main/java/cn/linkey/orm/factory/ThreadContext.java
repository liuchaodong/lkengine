package cn.linkey.orm.factory;



import java.sql.Connection;
import java.util.HashMap;

/**
 * 线程线别的共享对像类,本类为多例类，每一个http线程都应该持有一个本类的实例对像 本类不进行数据的初始化，初始化在BeanCtx中完成
 * 
 * @author Administrator 本类为多实例类
 */

public class ThreadContext {
    private Connection conn; // 数据库链接对像
    private boolean rollback; // true表示整个线程需要回滚,false表示提交
    private HashMap<String, Object> ctxMap = new HashMap<String, Object>(); // 设置一个全局的交换变量,可以在线程级别中进行使用和跨Bean交换数据

    /**
     * 获得线程级别的数据库链接对像
     * 
     * @return
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * 设置线程级别的数据库链接对像
     * 
     * @param conn
     */
    protected void setConnection(Connection conn) {
        this.conn = conn;
    }



    /**
     * 获取全局变量对像，按照Object进行返回
     * 
     * @return 返回对像
     */
    protected Object getCtxData(String key) {
        return ctxMap.get(key);
    }

    /**
     * 返回全局变量对像按字符串返回
     * 
     * @param key
     * @return
     */
    protected String getCtxDataStr(String key) {
        Object obj = ctxMap.get(key);
        if (obj == null) {
            return "";
        }
        else {
            return (String) obj;
        }
    }

    /**
     * 设置全局变量对像
     * @param key
     * @param obj
     */
    protected void setCtxData(String key, Object obj) {
        this.ctxMap.put(key, obj);
    }

    /**
     * 是否需要回滚
     * 
     * @return
     */
    protected boolean isRollBack() {
        return rollback;
    }

    /**
     * 设置回滚标记
     * 
     * @param rollBack
     */
    protected void setRollback(boolean rollBack) {
        this.rollback = rollBack;
    }

    protected void close() {
        Connection conn = getConnection();
        try {
            if (conn != null && !conn.isClosed()) {
                 conn.close();
            }
            else {
                 System.out.println("TE1.0  ThreadContext.close()链接不存在="+conn);
            }
        }
        catch (Exception e) {
             System.out.println("ThreadContext.close()数据库链接关闭出错!");
            e.printStackTrace();
             System.out.println("Context级别的数据库链接关闭出错!");
        }
        this.conn = null;
        if (this.ctxMap != null) {
            this.ctxMap.clear();
        }
    }
}
