package ru.urfu.messages.dto;

import java.util.List;

public class ItemsResponse<T> {

    private final List<T> items;

    public ItemsResponse(List<T> items) {
        this.items = items == null ? List.of() : List.copyOf(items);
    }

    public List<T> getItems() {
        return items;
    }
}
