package com.numpyninja.lms.util;

import com.numpyninja.lms.entity.AssignmentSubmit;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class AssignmentGradedEvent extends ApplicationEvent {
    private final transient AssignmentSubmit assignmentSubmit;

    public AssignmentGradedEvent(AssignmentSubmit assignmentSubmit) {
        super(assignmentSubmit);
        this.assignmentSubmit = assignmentSubmit;
    }
}
