package ru.practicum.shareit.item.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(Long owner);

    List<Item> findByOwner(Long owner, Pageable pageable);

    List<Item> findByRequestId(Long requestId);

    List<Item> findByRequestIdIn(@Param("ids") List<Long> ids);

    List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailable(String text, String text2, Boolean available, Pageable pageable);
}


