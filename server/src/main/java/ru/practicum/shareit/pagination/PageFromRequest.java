package ru.practicum.shareit.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageFromRequest extends PageRequest {

    private final int offset;

    public PageFromRequest(int offset, int size, Sort sort) {
        super(offset / size, size, sort);
        this.offset = offset;
    }

    public static PageFromRequest of(int offset, int size) {
        return new PageFromRequest(offset, size, Sort.unsorted());
    }

    public static PageFromRequest sortedOf(int offset, int size, Sort sort) {
        return new PageFromRequest(offset, size, sort);
    }

    @Override
    public long getOffset() {
        return offset;
    }
}
