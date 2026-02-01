/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventBus {
    public static final EventBus INSTANCE = new EventBus();
    private final Map<Class<? extends Event>, List<EventSubscriber>> subscriberMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void register(Object subscriber) {
        Method[] methods = subscriber.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(EventListener.class)) {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length == 1 && Event.class.isAssignableFrom(parameters[0])) {
                    Class<? extends Event> eventClass = (Class<? extends Event>) parameters[0];
                    method.setAccessible(true);

                    Consumer<Event> consumer = event -> {
                        try {
                            method.invoke(subscriber, event);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    };

                    subscriberMap.computeIfAbsent(eventClass, k -> new ArrayList<>())
                            .add(new EventSubscriber(subscriber, consumer));
                }
            }
        }
    }

    public void unregister(Object subscriber) {
        for (List<EventSubscriber> subscribers : subscriberMap.values()) {
            subscribers.removeIf(s -> s.owner == subscriber);
        }
    }

    public void post(Event event) {
        List<EventSubscriber> eventSubscribers = subscriberMap.get(event.getClass());
        if (eventSubscribers != null) {
            for (EventSubscriber sub : new ArrayList<>(eventSubscribers)) {
                if (event.isCancelled()) {
                    break;
                }
                sub.consumer.accept(event);
            }
        }
    }

    private static class EventSubscriber {
        private final Object owner;
        private final Consumer<Event> consumer;

        public EventSubscriber(Object owner, Consumer<Event> consumer) {
            this.owner = owner;
            this.consumer = consumer;
        }
    }
}