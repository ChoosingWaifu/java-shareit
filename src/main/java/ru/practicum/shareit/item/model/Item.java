package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Item {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
    private Long owner;

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
