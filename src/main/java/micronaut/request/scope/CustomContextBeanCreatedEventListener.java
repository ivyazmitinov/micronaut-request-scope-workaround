package micronaut.request.scope;

import io.micronaut.context.annotation.Context;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.scheduling.instrument.InstrumentedScheduledExecutorService;

import javax.inject.Inject;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

@Context
public class CustomContextBeanCreatedEventListener implements BeanCreatedEventListener<ScheduledExecutorService>
{

    @Inject
    private CustomContext<? super Object> context;

    @Override
    public ScheduledExecutorService onCreated(BeanCreatedEvent<ScheduledExecutorService> event)
    {
        return new InstrumentedScheduledExecutorService()
        {
            @Override
            public ScheduledExecutorService getTarget()
            {
                return event.getBean();
            }

            @Override
            public <T> Callable<T> instrument(Callable<T> task)
            {
                return context.currentContext()
                           .<Callable<T>>map(currentContext -> () -> context.with(currentContext, task))
                           .orElse(task);
            }

            @Override
            public Runnable instrument(Runnable command)
            {
                return context.currentContext()
                           .map(currentContext -> (Runnable) () -> context.with(currentContext, command))
                           .orElse(command);
            }
        };
    }
}
