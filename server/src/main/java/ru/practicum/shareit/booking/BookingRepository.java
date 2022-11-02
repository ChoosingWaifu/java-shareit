package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker(Long bookerId, Pageable pageable);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemId(Long itemId, Pageable pageable);

    List<Booking> findByItemIdAndStatus(Long itemId, BookingStatus status);
}
