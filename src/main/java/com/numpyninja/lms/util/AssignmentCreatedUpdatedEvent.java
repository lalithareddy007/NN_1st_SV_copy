package com.numpyninja.lms.util;

import com.numpyninja.lms.entity.Assignment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AssignmentCreatedUpdatedEvent extends ApplicationEvent {

    private final transient Assignment newAssignment;

    public AssignmentCreatedUpdatedEvent (Assignment newAssignment){
        super(newAssignment);
        this.newAssignment = newAssignment;
    }


}
