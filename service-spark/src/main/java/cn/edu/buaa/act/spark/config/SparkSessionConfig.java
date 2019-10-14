package cn.edu.buaa.act.spark.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkSessionConfig {
    @Value("${spark.app.name:word-count}")
    private String appName;
    @Value("${spark.master.uri:local}")
    private String masterUri;


    private final String sparkCluster = "spark://sparkspring.local:7077";
    private final String sparklocal = "local[*]";
    private final String tempWareHouse = "/user/apps/Spark";

    private JavaSparkContext sparkContext;
    private SparkSession sparkSession;

    private JavaSparkContext getOrCreateSparkContext() {
        if (this.sparkContext != null) {
            return this.sparkContext;
        }
        return new JavaSparkContext(getSparkConf());
    }

    public SparkSession getOrCreateSparkSession() {
        if (this.sparkSession != null) {
            return this.sparkSession;
        }
        return getSparkSession();
    }

    private SparkConf getSparkConf() {
        return new SparkConf()
                .setAppName("SparkSpringRest")
                .setMaster(sparkCluster);
    }

    private SparkSession getSparkSession() {
        return SparkSession.builder()
                .appName("SparkSpringRest")
                .master(sparklocal)//(sparkCluster)
                .config("spark.sql.warehouse.dir", tempWareHouse)
            //    .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/test.myCollection")
           //     .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/test.myCollection")
                .getOrCreate();
    }
}