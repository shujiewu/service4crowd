//package cn.edu.buaa.act.spark.controller;
//
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.sql.SparkSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class SparkJobController {
//
//    private final String sparkCluster = "spark://sparkedcache.local:7077";
//    private final String sparklocal = "local[*]";
//    private final String tempWareHouse = "/user/apps/Spark";
//
//    private JavaSparkContext sparkContext;
//    private SparkSession sparkSession;
//
//    @Autowired
//    private SparkDataLoader sparkDataLoader;
//
//
//    @RequestMapping("/")
//    public String index() {
//        return "Hey!! What's up? Want a sparky cahce?";
//    }
//
//
//    @RequestMapping(method = RequestMethod.GET, path = "/sample/spark/load-cache")
//    public String loadDBDataToSpark() {
//        String rtnCnt = new String();
//        rtnCnt = sparkDataLoader.loadCSV2DB(getOrCreateSparkSession());
//        return "Data loaded ==>  " + rtnCnt;
//    }
//
//
//    private JavaSparkContext getOrCreateSparkContext() {
//        if (this.sparkContext != null)
//            return this.sparkContext;
//        return new JavaSparkContext(getSparkConf());
//    }
//
//    private SparkSession getOrCreateSparkSession() {
//        if (this.sparkSession != null)
//            return this.sparkSession;
//        return getSparkSession();
//    }
//
//    private SparkConf getSparkConf() {
//        return new SparkConf()
//                .setAppName("SparkCacheLoader")
//                .setMaster(sparkCluster);
//    }
//
//    private SparkSession getSparkSession() {
//        return SparkSession.builder()
//                .appName("SparkSpring")
//                .master(sparklocal)//(sparkCluster)
//                //  .config("spark.sql.warehouse.dir", tempWareHouse)
//                .getOrCreate();
//    }
//
//}