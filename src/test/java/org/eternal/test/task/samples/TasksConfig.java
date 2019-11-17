package org.eternal.test.task.samples;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.eternal.task.Tasks;
import org.eternal.task.builder.Builder;
import org.eternal.task.builder.Builder.FlowBuilder;
import org.eternal.task.builder.Builder.TaskBuilder;

public class TasksConfig {

    private final String taskName;

    public TasksConfig(String taskName) {
        this.taskName = taskName;
    }

    private List<Builder> builders = new ArrayList<>();

    public TasksConfig addTask(Consumer<TaskBuilder> consumer) {
        TaskBuilder b = TaskBuilder.generate(taskName);
        builders.add(b.gotta());
        consumer.accept(b);
        return this;
    }

    public TasksConfig addFlow(String flowName, Consumer<FlowBuilder> consumer) {
        FlowBuilder b = FlowBuilder.generate(flowName);
        builders.add(b.gotta());
        consumer.accept(b);
        return this;
    }

    private Tasks tasks = null;

    public Tasks getTasks() {
        return Optional.ofNullable(tasks).orElseGet(() -> {
            try {
                tasks = Tasks.generate(builders.toArray(new Builder[builders.size()]));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return tasks;
        });
    }
}
