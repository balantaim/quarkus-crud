package com.martinatanasov.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.function.Function;

@RegisterForReflection
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public <R> PageResponse<R> map(Function<T, R> mapper) {
        return new PageResponse<>(content.stream().map(mapper).toList(),
                page, size, totalElements, totalPages);
    }

}
