package org.eternal.test.task.samples.s03;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eternal.task.handler.FlowHandler;
import org.eternal.task.handler.TaskService;

public class TestSHandler extends FlowHandler {

    final String name;

    TestSHandler(String name) {
        this.name = name;
    }

    @Override
    public String getFlowHandlerName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <V, S extends TaskService> S getTaskService(Optional<V> args) {

        String name = (String) args.orElse((V) "servicea");

        System.out.println("-- service name is " + name);

        return (S) Optional.ofNullable(services.get(name)).orElseGet(() -> {
            TaskService s = null;
            if ("servicea".equals(name)) {
                s = new TestServiceA();
            } else if ("serviceb".equals(name)) {
                s = new TestServiceB();
            } else if ("servicec".equals(name)) {
                s = new TestServiceC();
            } else if ("serviced".equals(name)) {
                s = new TestServiceD();
            } else if ("servicee".equals(name)) {
                s = new TestServiceE();
            }
            services.put(name, s);
            return s;
        });
    }

    Map<String, TaskService> services = new HashMap<>();

}
