package ru.practicum.shareit.item.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(Long owner);

    List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailable(String text, String text2, Boolean available);
}


