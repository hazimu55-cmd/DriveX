package com.carrental.exception;

public class ResourceNotFoundException extends CarRentalException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }
    public ResourceNotFoundException(String resource, String field, String value) {
        super(resource + " not found with " + field + ": " + value);
    }
}
