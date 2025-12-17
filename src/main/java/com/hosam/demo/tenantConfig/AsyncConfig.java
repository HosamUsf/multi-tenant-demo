package com.hosam.demo.tenantConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


/**
 * AsyncConfig - Propagates tenant context to @Async methods
 * <p>
 * Solves the problem: ThreadLocal tenant context gets lost in async methods
 * <p>
 * This config captures tenant ID from original thread and copies it to new async thread,
 * allowing all @Async methods to access the correct tenant without code changes.
 * @author HosamUsf
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);

        executor.setTaskDecorator(new TaskDecorator() {
            @Override
            public Runnable decorate(Runnable runnable) {
                // Capture context from calling thread
                String tenantId = TenantContext.getCurrentTenant();

                // Return a wrapped Runnable that sets up the context before execution
                return () -> {
                    try {
                        // Set context in the worker thread
                        TenantContext.setCurrentTenant(tenantId);
                        // Execute the actual task
                        runnable.run();
                    } finally {
                        // Clean up the context
                        TenantContext.clear();
                    }
                };
            }
        });

        executor.initialize();
        return executor;
    }
}
