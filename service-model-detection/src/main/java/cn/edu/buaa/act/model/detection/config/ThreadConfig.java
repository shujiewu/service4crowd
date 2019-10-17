package cn.edu.buaa.act.model.detection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wsj
 */
@Configuration
public class ThreadConfig {

    @Bean("asyncExecutor")
    public Executor asyncExecutor2() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 核心线程数10：线程池创建时候初始化的线程数
        executor.setMaxPoolSize(20); // 最大线程数20：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setQueueCapacity(200); // 缓冲队列200：用来缓冲执行任务的队列
        executor.setKeepAliveSeconds(60); // 允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setThreadNamePrefix("asyncExecutor-"); // 线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}