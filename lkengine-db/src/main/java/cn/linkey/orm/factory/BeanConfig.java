package cn.linkey.orm.factory;


import cn.linkey.orm.doc.Document;

import java.util.HashMap;

/**
 * 本类主要根据beanid返回具体的类路径 <br />
 * 本类为单例静态类
 * 
 * @author lch
 */
public class BeanConfig {

    @SuppressWarnings("unchecked")
    public static HashMap<String, String> getClassPath(String beanid) {
           HashMap<String, String> beancfg = new HashMap<String, String>();
            String sql = "select classPath,singleton from BPM_BeanConfig where Beanid='" + beanid + "'";
            System.out.println("单独初始化Bean不在缓存中 Beanid=" + beanid);
            Document doc = BeanCtx.getRdbBean().getDocumentBySql(sql);
            beancfg.put("classPath", doc.g("classPath"));
            beancfg.put("singleton", doc.g("singleton"));
            if (beancfg == null || beancfg.get("classPath") == null) {
                System.out.println("在BPM_BeanConfig配置表中没有找到(" + beanid + ")的配置信息...");
            }
            return beancfg;
        }
    //}

}
