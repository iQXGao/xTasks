package org.eternal.test.task.samples.s03;

import org.eternal.eventbus.EventResult;
import org.eternal.task.TaskEvent;
import org.eternal.task.handler.TaskService;

public class TestServiceD extends TaskService.Impl {

    @Override
    protected EventResult execute(TaskEvent event, EventResult result) {

        System.out.println("-- || " + event.toString());
        signNext(  event) ;
        return result;
    }

    void signNext(TaskEvent event) {
        event.withArgs("servicee");
    }

}
