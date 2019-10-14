package cn.edu.buaa.act.workflow.config;


import cn.edu.buaa.act.workflow.common.FormFileType;
import org.activiti.engine.*;
import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.impl.asyncexecutor.AsyncExecutor;
import org.activiti.engine.impl.asyncexecutor.DefaultAsyncJobExecutor;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.parse.BpmnParseHandler;
import org.activiti.engine.runtime.Clock;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.engine.FormEngineConfiguration;
import org.activiti.form.engine.configurator.FormEngineConfigurator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class ActivitiEngineConfiguration extends AbstractProcessEngineAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(ActivitiEngineConfiguration.class);
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    @Autowired
    private Environment environment;
    
    @Bean(name="processEngine")
    public ProcessEngineFactoryBean processEngineFactoryBean() {
        ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        return factoryBean;
    }
    
    public ProcessEngine processEngine() {
        try {
            return processEngineFactoryBean().getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Bean
    public AsyncExecutor asyncExecutor() {
        DefaultAsyncJobExecutor asyncExecutor = new DefaultAsyncJobExecutor();
        asyncExecutor.setDefaultAsyncJobAcquireWaitTimeInMillis(5000);
        asyncExecutor.setDefaultTimerJobAcquireWaitTimeInMillis(5000);
        asyncExecutor.setAsyncJobLockTimeInMillis(1000*300);
       // asyncExecutor.setAsyncJobLockTimeInMillis(10000);
        asyncExecutor.setTimerLockTimeInMillis(5000);
        asyncExecutor.setSecondsToWaitOnShutdown(0);
        asyncExecutor.setResetExpiredJobsInterval(2000);
        asyncExecutor.setMaxAsyncJobsDuePerAcquisition(1);
        asyncExecutor.setCorePoolSize(1);
//      asyncExecutor.setMaxPoolSize(10);
//      asyncExecutor.setQueueSize(30);
//      asyncExecutor.setResetExpiredJobsPageSize(10);
//      asyncExecutor.setResetExpiredJobsInterval(5);
//      asyncExecutor.setDefaultQueueSizeFullWaitTimeInMillis();
        return asyncExecutor;
    }

    @Bean(name="processEngineConfiguration")
    public ProcessEngineConfigurationImpl processEngineConfiguration() {
    	SpringProcessEngineConfiguration processEngineConfiguration = new SpringProcessEngineConfiguration();
    	processEngineConfiguration.setDataSource(dataSource);
    	processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
    	processEngineConfiguration.setTransactionManager(transactionManager);
        processEngineConfiguration.setAsyncExecutorActivate(true);
    	processEngineConfiguration.setAsyncExecutor(asyncExecutor());
//        processEngineConfiguration.setAsyncExecutorCorePoolSize(10);
//        processEngineConfiguration.setAsyncExecutorMaxPoolSize(20);
//        processEngineConfiguration.setAsyncExecutorMaxAsyncJobsDuePerAcquisition(10);



//    	processEngineConfiguration.setLockTimeAsyncJobWaitTime(3);
//    	processEngineConfiguration.setAsyncExecutorAsyncJobLockTimeInMillis(50000000);
//    	processEngineConfiguration.setAsyncExecutorTimerLockTimeInMillis(5000);
//    	processEngineConfiguration.setAsyncExecutorDefaultAsyncJobAcquireWaitTime(3000);
//    	processEngineConfiguration.setAsyncExecutorSecondsToWaitOnShutdown(3);
//    	processEngineConfiguration.setAsyncExecutorResetExpiredJobsInterval(2000);

//        processEngineConfiguration.getAsyncExecutor().setDefaultTimerJobAcquireWaitTimeInMillis(3000);
//        processEngineConfiguration.getAsyncExecutor().setAsyncJobLockTimeInMillis(5000);
//        processEngineConfiguration.getAsyncExecutor().setTimerLockTimeInMillis(5000);
//        processEngineConfiguration.getAsyncExecutor().setDefaultAsyncJobAcquireWaitTimeInMillis(3000);
//        processEngineConfiguration.getAsyncExecutor().setResetExpiredJobsInterval(2000);
//        System.out.println(processEngineConfiguration.getAsyncExecutorAsyncJobLockTimeInMillis()+"  "+processEngineConfiguration.getAsyncExecutor().getAsyncJobLockTimeInMillis());
    	String emailHost = environment.getProperty("email.host");
    	if (StringUtils.isNotEmpty(emailHost)) {
        	processEngineConfiguration.setMailServerHost(emailHost);
        	processEngineConfiguration.setMailServerPort(environment.getRequiredProperty("email.port", Integer.class));
        	
        	Boolean useCredentials = environment.getProperty("email.useCredentials", Boolean.class);
            if (Boolean.TRUE.equals(useCredentials)) {
                processEngineConfiguration.setMailServerUsername(environment.getProperty("email.username"));
                processEngineConfiguration.setMailServerPassword(environment.getProperty("email.password"));
            }
            
            Boolean emailSSL = environment.getProperty("email.ssl", Boolean.class);
            if (emailSSL != null) {
              processEngineConfiguration.setMailServerUseSSL(emailSSL.booleanValue());
            }
            
            Boolean emailTLS = environment.getProperty("email.tls", Boolean.class);
            if (emailTLS != null) {
              processEngineConfiguration.setMailServerUseTLS(emailTLS.booleanValue());
            }
    	}

    	processEngineConfiguration.setProcessDefinitionCacheLimit(environment.getProperty("activiti.process-definitions.cache.max", Integer.class, 128));

    	processEngineConfiguration.setEnableSafeBpmnXml(true);
    	// 预处理
    	List<BpmnParseHandler> preParseHandlers = new ArrayList<BpmnParseHandler>();
    	processEngineConfiguration.setPreBpmnParseHandlers(preParseHandlers);
    	
    	FormEngineConfiguration formEngineConfiguration = new FormEngineConfiguration();
    	formEngineConfiguration.setDataSource(dataSource);
    	
    	FormEngineConfigurator formEngineConfigurator = new FormEngineConfigurator();
    	formEngineConfigurator.setFormEngineConfiguration(formEngineConfiguration);
    	processEngineConfiguration.addConfigurator(formEngineConfigurator);

    	return processEngineConfiguration;
    }

    @Bean(name="clock")
    @DependsOn("processEngine")
    public Clock getClock() {
    	return processEngineConfiguration().getClock();
    }
    
    @Bean
    public RepositoryService repositoryService() {
    	return processEngine().getRepositoryService();
    }
    
    @Bean
    public RuntimeService runtimeService() {
    	return processEngine().getRuntimeService();
    }
    
    @Bean
    public TaskService taskService() {
        return processEngine().getTaskService();
    }
    
    @Bean
    public HistoryService historyService() {
    	return processEngine().getHistoryService();
    }
    
    @Bean
    public FormService formService() {
    	return processEngine().getFormService();
    }
    
    @Bean
    public IdentityService identityService() {
    	return processEngine().getIdentityService();
    }
    
    @Bean
    public ManagementService managementService() {
    	return processEngine().getManagementService();
    }

    @Bean
    public  DynamicBpmnService dynamicBpmnService() {return processEngine().getDynamicBpmnService();}
    @Bean
    public FormRepositoryService formEngineRepositoryService() {
      return processEngine().getFormEngineRepositoryService();
    }
    
    @Bean
    public org.activiti.form.api.FormService formEngineFormService() {
      return processEngine().getFormEngineFormService();
    }

    @Bean
    public BeanPostProcessor activitiConfigurer() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof ProcessEngineConfigurationImpl) {
                    List<AbstractFormType> customFormTypes = Arrays.<AbstractFormType>asList(new FormFileType());
                    ((ProcessEngineConfigurationImpl)bean).setCustomFormTypes(customFormTypes);
                }
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }
        };

    }
}
