package com.smartclinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {
    
    private static final int POOL_SIZE = 10;
    private static final String THREAD_NAME_PREFIX = "smartclinic-scheduler-";
    private static final int AWAIT_TERMINATION_SECONDS = 60;
    
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
    }
    
    /**
     * Configure thread pool for scheduled tasks
     * Used for:
     * - Real-time ETA correction
     * - Statistics updates
     * - Model training
     * - Data cleanup
     * - Analytics processing
     */
    public Executor taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(POOL_SIZE);
        scheduler.setThreadNamePrefix(THREAD_NAME_PREFIX);
        scheduler.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setErrorHandler(throwable -> {
            // Log the error but don't crash the scheduler
            System.err.println("Scheduled task execution failed: " + throwable.getMessage());
            throwable.printStackTrace();
        });
        scheduler.initialize();
        return scheduler;
    }
}