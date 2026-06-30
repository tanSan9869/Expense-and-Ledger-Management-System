package com.ELSystem.elsystem.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final Long resourceId;
    public ResourceNotFoundException(String resourceName,Long resourceId) {
        super(resourceName + "not found with id: " + resourceId);
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }
}
