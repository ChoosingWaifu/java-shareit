package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDto {

        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;

        @Override
        public String toString() {
                return "ItemDto{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        ", description='" + description + '\'' +
                        ", available=" + available +
                        ", requestId=" + requestId +
                        '}';
        }
}
