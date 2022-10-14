package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Valid
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "starttime")
    private LocalDateTime start;

    @Column(name = "endtime")
    private LocalDateTime end;

    @Column(name = "itemid")
    private Long itemId;

    @Column(name = "booker")
    private Long booker;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", booker=" + booker +
                ", status=" + status +
                '}';
    }
}
