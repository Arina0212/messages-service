package ru.urfu.messages.dto;

import java.util.List;

public class MessagesPage {

    private final List<MessageResponse> items;
    private final Long next;
    private final int limit;

    public MessagesPage(List<MessageResponse> items, Long next, int limit) {
        this.items = items == null ? List.of() : List.copyOf(items);
        this.next = next;
        this.limit = limit;
    }

    public List<MessageResponse> getItems() {
        return items;
    }

    public Long getNext() {
        return next;
    }

    public int getLimit() {
        return limit;
    }
}
