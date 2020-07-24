package cn.linkey.orm.test;

import cn.linkey.orm.dao.Rdb;
import cn.linkey.orm.doc.Document;
import cn.linkey.orm.factory.BeanCtx;

public class Test {
    public static void main(String[] args){
        try {
            Rdb rdb = BeanCtx.getRdbBean();
            //System.out.println(rdb.getConnection());
            //Document doc = rdb.getDocumentBySql("select * from bpm_orguserlist limit 1");
            Document document = BeanCtx.getDocumentBean("bpm_orguserlist");
            document.s("WF_OrUnid",rdb.getNewUnid());
            document.s("WF_AddName","qiumeng");
            document.s("CnName","邱梦");
            document.s("Password","1a1dc91c907325c69271ddf0c944bc72");
            //document.save();
           /* HashMap<String,String> hashMap=rdb.getRuleCodeMapByRuleNum("R_S005_B016","");

            for (Map.Entry<String, String > entry : hashMap.entrySet()){
                System.out.println(entry.getKey());

            }*/
            System.out.println(rdb.getRuleCodeMapByRuleNum("R_S005_B016",""));


           // System.out.println(BeanCtx.getRdbBean());
            /*System.out.println((rdb.getConnection()).equals(BeanCtx.getConnection()));
            System.out.println((rdb.getConnection()).equals(JdbcUtil.getConnection()));

            System.out.println(document.copyAllItems(doc));*/


        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
