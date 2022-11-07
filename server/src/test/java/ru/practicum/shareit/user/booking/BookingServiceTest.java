package ru.practicum.shareit.user.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository mockRepository;

    private final User user1 = new User(1L,"A","waifu@yandex.ru");
    private final Item item1 = new Item(1L,"item1","desc1",true,1L,null, new ArrayList<>());
    private final Booking booking1 = new Booking(1L, LocalDateTime.now().plusMinutes(60),
            LocalDateTime.now().plusMinutes(100), 1L, 1L, null);

    @Test
    void getById() {
        Mockito.when(mockRepository.findById(any()))
                .thenReturn(Optional.of(booking1));
        Assertions.assertEquals(booking1.toString(), bookingService.getById(booking1.getId()).toString());
    }

    @Test
    void bookingStateFilterTest() {
        BookingPostDto waiting = BookingMapper.toPostDto(booking1, item1, user1);
        waiting.setStatus(BookingStatus.WAITING);
        BookingPostDto rejected = BookingMapper.toPostDto(booking1, item1, user1);
        rejected.setStatus(BookingStatus.REJECTED);
        BookingPostDto past = BookingMapper.toPostDto(booking1, item1, user1);
        past.setStart(LocalDateTime.MIN);
        past.setEnd(LocalDateTime.MIN);
        BookingPostDto future = BookingMapper.toPostDto(booking1, item1, user1);
        future.setStart(LocalDateTime.MAX);
        BookingPostDto current = BookingMapper.toPostDto(booking1, item1, user1);
        current.setStart(LocalDateTime.MIN);

        List<BookingPostDto> dtoList = new ArrayList<>(List.of(waiting, rejected, past, future, current));
        List<BookingPostDto> resultAll = bookingService.bookingStateFilter(State.ALL, dtoList);
        Assertions.assertEquals(dtoList, resultAll);
        List<BookingPostDto> resultWaiting = bookingService.bookingStateFilter(State.WAITING, dtoList);
        Assertions.assertEquals(List.of(waiting), resultWaiting);
        List<BookingPostDto> resultRejected = bookingService.bookingStateFilter(State.REJECTED, dtoList);
        Assertions.assertEquals(List.of(rejected), resultRejected);
        List<BookingPostDto> resultPast = bookingService.bookingStateFilter(State.PAST, dtoList);
        Assertions.assertEquals(List.of(past), resultPast);
        List<BookingPostDto> resultCurrent = bookingService.bookingStateFilter(State.CURRENT, dtoList);
        Assertions.assertEquals(List.of(current), resultCurrent);
        List<BookingPostDto> resultFuture = bookingService.bookingStateFilter(State.FUTURE, dtoList);
        Assertions.assertEquals(List.of(waiting, rejected, future), resultFuture);
    }

}
