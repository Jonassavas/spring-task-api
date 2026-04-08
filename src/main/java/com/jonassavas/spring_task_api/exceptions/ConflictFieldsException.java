package com.jonassavas.spring_task_api.exceptions;

import java.util.Map;

public class ConflictFieldsException extends RuntimeException {
    private final Map<String, String> conflicts;

    public ConflictFieldsException(Map<String, String> conflicts) {
        super("Some fields are already in use");
        this.conflicts = conflicts;
    }

    public Map<String, String> getConflicts() {
        return conflicts;
    }
}