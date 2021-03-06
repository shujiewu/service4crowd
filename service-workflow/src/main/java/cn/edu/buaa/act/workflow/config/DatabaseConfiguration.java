package cn.edu.buaa.act.workflow.config;

//import com.mchange.v2.c3p0.ComboPooledDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.activiti.engine.ActivitiException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
// import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;

//import javax.inject.Inject;

/**
 * @author wsj
 * 2.0 暂时不需要
 */ //
// @Configuration
//@EnableJpaRepositories({ "cn.edu.buaa.act.workflow.repository" })
//@EnableTransactionManagement
//public class DatabaseConfiguration {
//
//  private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);
//
//  protected static final String LIQUIBASE_CHANGELOG_PREFIX = "ACT_DE_";
//
//  @Autowired
//  private Environment env;
//
//  @Bean
//  public DataSource dataSource() {
//    log.info("Configuring Datasource");
//
//    String dataSourceJndiName = env.getProperty("datasource.jndi.name");
//    if (StringUtils.isNotEmpty(dataSourceJndiName)) {
//
//      log.info("Using jndi datasource '" + dataSourceJndiName + "'");
//      JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
//      dsLookup.setResourceRef(env.getProperty("datasource.jndi.resourceRef", Boolean.class, Boolean.TRUE));
//      DataSource dataSource = dsLookup.getDataSource(dataSourceJndiName);
//      return dataSource;
//
//    } else {
//
//      String dataSourceDriver = env.getProperty("spring.datasource.driver-class-name", "org.h2.Driver");
//      String dataSourceUrl = env.getProperty("spring.datasource.url", "jdbc:h2:mem:activiti;DB_CLOSE_DELAY=-1");
//
//      String dataSourceUsername = env.getProperty("spring.datasource.username", "sa");
//      String dataSourcePassword = env.getProperty("spring.datasource.password", "");
//
//      Integer minPoolSize = env.getProperty("datasource.min-pool-size", Integer.class);
//      if (minPoolSize == null) {
//        minPoolSize = 10;
//      }
//
//      Integer maxPoolSize = env.getProperty("datasource.max-pool-size", Integer.class);
//      if (maxPoolSize == null) {
//        maxPoolSize = 100;
//      }
//
//      Integer acquireIncrement = env.getProperty("datasource.acquire-increment", Integer.class);
//      if (acquireIncrement == null) {
//        acquireIncrement = 5;
//      }
//
//      String preferredTestQuery = env.getProperty("datasource.preferred-test-query");
//
//      Boolean testConnectionOnCheckin = env.getProperty("datasource.test-connection-on-checkin", Boolean.class);
//      if (testConnectionOnCheckin == null) {
//        testConnectionOnCheckin = true;
//      }
//
//      Boolean testConnectionOnCheckOut = env.getProperty("datasource.test-connection-on-checkout", Boolean.class);
//      if (testConnectionOnCheckOut == null) {
//        testConnectionOnCheckOut = true;
//      }
//
//      Integer maxIdleTime = env.getProperty("datasource.max-idle-time", Integer.class);
//      if (maxIdleTime == null) {
//        maxIdleTime = 1800;
//      }
//
//      Integer maxIdleTimeExcessConnections = env.getProperty("datasource.max-idle-time-excess-connections", Integer.class);
//      if (maxIdleTimeExcessConnections == null) {
//        maxIdleTimeExcessConnections = 1800;
//      }
//
//      if (log.isInfoEnabled()) {
//        log.info("Configuring Datasource with following properties (omitted password for security)");
//        log.info("datasource driver: " + dataSourceDriver);
//        log.info("datasource url : " + dataSourceUrl);
//        log.info("datasource user name : " + dataSourceUsername);
//        log.info("Min pool size | Max pool size | acquire increment : " + minPoolSize + " | " + maxPoolSize + " | " + acquireIncrement);
//      }
//
//      ComboPooledDataSource ds = new ComboPooledDataSource();
//      try {
//        ds.setDriverClass(dataSourceDriver);
//      } catch (PropertyVetoException e) {
//        log.error("Could not set Jdbc Driver class", e);
//        return null;
//      }
//
//      ds.setJdbcUrl(dataSourceUrl);
//      ds.setUser(dataSourceUsername);
//      ds.setPassword(dataSourcePassword);
//      ds.setMinPoolSize(minPoolSize);
//      ds.setMaxPoolSize(maxPoolSize);
//      ds.setAcquireIncrement(acquireIncrement);
//      if (preferredTestQuery != null) {
//        ds.setPreferredTestQuery(preferredTestQuery);
//      }
//      ds.setTestConnectionOnCheckin(testConnectionOnCheckin);
//      ds.setTestConnectionOnCheckout(testConnectionOnCheckOut);
//      ds.setMaxIdleTimeExcessConnections(maxIdleTimeExcessConnections);
//      ds.setMaxIdleTime(maxIdleTime);
//
//      return ds;
//    }
//  }
//
//  @Bean(name = "entityManagerFactory")
//  public EntityManagerFactory entityManagerFactory() {
//    log.info("Configuring EntityManager");
//    LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
//    lcemfb.setPersistenceProvider(new HibernatePersistenceProvider());
//    lcemfb.setPersistenceUnitName("persistenceUnit");
//    lcemfb.setDataSource(dataSource());
//    lcemfb.setJpaDialect(new HibernateJpaDialect());
//    lcemfb.setJpaVendorAdapter(jpaVendorAdapter());
//
//    Properties jpaProperties = new Properties();
//    jpaProperties.put("hibernate.cache.use_second_level_cache", false);
//    jpaProperties.put("hibernate.generate_statistics", env.getProperty("hibernate.generate_statistics", Boolean.class, false));
//    // jpaProperties.put("hibernate.ddl-auto","update");
//    lcemfb.setJpaProperties(jpaProperties);
//    lcemfb.setPackagesToScan("cn.edu.buaa.act.workflow.domain");
//    lcemfb.afterPropertiesSet();
//    return lcemfb.getObject();
//  }
//
//  @Bean
//  public JpaVendorAdapter jpaVendorAdapter() {
//    HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
//    jpaVendorAdapter.setShowSql(env.getProperty("hibernate.show_sql", Boolean.class, false));
//    jpaVendorAdapter.setDatabasePlatform(env.getProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect"));
//    return jpaVendorAdapter;
//  }
//
////  @Bean
////  public HibernateExceptionTranslator hibernateExceptionTranslator() {
////    return new HibernateExceptionTranslator();
////  }
//
//  @Bean(name = "transactionManager")
//  public PlatformTransactionManager annotationDrivenTransactionManager() {
//    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
//    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory());
//    return jpaTransactionManager;
//  }
//
//  @Bean(name = "liquibase")
//  public Liquibase liquibase() {
//    log.info("Configuring Liquibase");
//
//    try {
//      DatabaseConnection connection = new JdbcConnection(dataSource().getConnection());
//      Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
//      database.setDatabaseChangeLogTableName(LIQUIBASE_CHANGELOG_PREFIX+database.getDatabaseChangeLogTableName());
//      database.setDatabaseChangeLogLockTableName(LIQUIBASE_CHANGELOG_PREFIX+database.getDatabaseChangeLogLockTableName());
//
//      Liquibase liquibase = new Liquibase("activiti-app-db-changelog.xml", new ClassLoaderResourceAccessor(), database);
//      liquibase.update("activiti");
//      return liquibase;
//
//    } catch (Exception e) {
//      throw new ActivitiException("Error creating liquibase database");
//    }
//  }
//
//  @Bean
//  public JdbcTemplate jdbcTemplate() {
//    return new JdbcTemplate(dataSource());
//  }
//
//  @Bean
//  public TransactionTemplate transactionTemplate() {
//    return new TransactionTemplate(annotationDrivenTransactionManager());
//  }
//
//}
