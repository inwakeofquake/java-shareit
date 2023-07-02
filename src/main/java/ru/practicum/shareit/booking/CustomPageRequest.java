package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {

    public CustomPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
    }

    public static PageRequest of(int from, int size, Sort sort) {
        return new CustomPageRequest(from, size, sort);
    }
}

