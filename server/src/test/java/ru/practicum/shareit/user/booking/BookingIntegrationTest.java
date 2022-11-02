package ru.practicum.shareit.user.booking;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.StringToStateConverter;
import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnavailableItemException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:share-it")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class BookingIntegrationTest {

    private BookingService service;
    private BookingRepository repository;

    private UserRepository userRepository;
    private ItemRepository itemRepository;

    private final StringToStateConverter toStateConverter;

    private final User user1 = new User(1L,"A","waifu@yandex.ru");
    private final User user2 = new User(2L,"B","waifu2@yandex.ru");
    private final Item item1 = new Item(1L,"item1","desc1",true,1L,null, new ArrayList<>());
    private final Item item2 = new Item(2L,"item2","desc2",true,2L,null, new ArrayList<>());
    private final BookingDto bookingDto1 = new BookingDto(LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(100), item1.getId());
    private final BookingDto bookingDto2 = new BookingDto(LocalDateTime.now().plusMinutes(600), LocalDateTime.now().plusMinutes(1200), item1.getId());


    @BeforeEach
    void beforeEach() {
        service = new BookingServiceImpl(repository, itemRepository, userRepository, toStateConverter);
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    void post() {
        BookingPostDto result = service.post(bookingDto1, user2.getId());
        Assertions.assertEquals(bookingDto1.getStart(), result.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), result.getEnd());
        Assertions.assertEquals(bookingDto1.getItemId(), result.getItem().getId());

        Assertions.assertThrows(InsufficientRightsException.class, () -> service.post(bookingDto1, 1L));
        Assertions.assertThrows(NotFoundException.class, () -> service.post(bookingDto1, 666L));

        Item unavailable = new Item(3L,"item1","desc1",false,1L,null, new ArrayList<>());
        itemRepository.save(unavailable);
        BookingDto unavailableItem = new BookingDto(LocalDateTime.now().plusMinutes(60),
                LocalDateTime.now().plusMinutes(100), unavailable.getId());
        Assertions.assertThrows(UnavailableItemException.class, () -> service.post(unavailableItem, user2.getId()));

        bookingDto1.setEnd(LocalDateTime.MIN);
        Assertions.assertThrows(ValidationException.class, () -> service.post(bookingDto1, user2.getId()));
        bookingDto1.setItemId(100L);
        Assertions.assertThrows(NotFoundException.class, () -> service.post(bookingDto1, user2.getId()));
    }

    @Test
    void approve() {
        service.post(bookingDto1, user2.getId());
        Assertions.assertThrows(InsufficientRightsException.class, () -> service.approve(user2.getId(), 1L, true));
        BookingPostDto result = service.approve(user1.getId(), 1L, true);

        Assertions.assertEquals(bookingDto1.getStart(), result.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), result.getEnd());
        Assertions.assertEquals(bookingDto1.getItemId(), result.getItem().getId());
        Assertions.assertEquals(result.getStatus(), BookingStatus.APPROVED);

        Assertions.assertThrows(ValidationException.class, () -> service.approve(user1.getId(), 1L, true));

        service.post(bookingDto2, user2.getId());
        itemRepository.deleteById(item1.getId());
        Assertions.assertThrows(NotFoundException.class, () -> service.approve(user1.getId(), 2L, true));
        userRepository.deleteById(user2.getId());
        Assertions.assertThrows(NotFoundException.class, () -> service.approve(user1.getId(), 2L, true));

    }

    @Test
    void getDtoById() {
        Assertions.assertThrows(NotFoundException.class, () -> service.getDtoById(1L, 666L));

        service.post(bookingDto1, user2.getId());
        BookingPostDto result = service.getDtoById(1L, user1.getId());
        Assertions.assertEquals(bookingDto1.getStart(), result.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), result.getEnd());
        Assertions.assertEquals(bookingDto1.getItemId(), result.getItem().getId());
        Assertions.assertEquals(result.getStatus(), BookingStatus.WAITING);

        User wrongUser = new User(3L,"C","waifu3@yandex.ru");
        userRepository.save(wrongUser);
        Assertions.assertThrows(InsufficientRightsException.class, () -> service.getDtoById(1L, wrongUser.getId()));

        itemRepository.deleteById(item1.getId());
        Assertions.assertThrows(NotFoundException.class, () -> service.getDtoById(1L, user2.getId()));
        userRepository.deleteById(user2.getId());
        Assertions.assertThrows(NotFoundException.class, () -> service.getDtoById(1L, user2.getId()));
    }

    @Test
    void getUserBookings() {
        Assertions.assertThrows(NotFoundException.class, () -> service.getUserBookings(666L,"ALL", 0, 20));

        service.post(bookingDto1, user2.getId());
        List<BookingPostDto> result = service.getUserBookings(user2.getId(), "ALL", 0, 20);

        Assertions.assertEquals(bookingDto1.getStart(), result.get(0).getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), result.get(0).getEnd());
        Assertions.assertEquals(bookingDto1.getItemId(), result.get(0).getItem().getId());
        Assertions.assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);

        itemRepository.deleteById(bookingDto1.getItemId());
        Assertions.assertThrows(NotFoundException.class, () -> service.getUserBookings(user2.getId(),"ALL", 0, 20));
    }

    @Test
    void getItemsBookings() {
        Assertions.assertThrows(NotFoundException.class, () -> service.getItemsBookings(666L,"ALL", 0, 20));

        service.post(bookingDto1, user2.getId());
        List<BookingPostDto> result = service.getItemsBookings(user1.getId(), "ALL", 0, 20);
        Assertions.assertEquals(bookingDto1.getStart(), result.get(0).getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), result.get(0).getEnd());
        Assertions.assertEquals(bookingDto1.getItemId(), result.get(0).getItem().getId());
        Assertions.assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);

        userRepository.deleteById(user2.getId());
        Assertions.assertThrows(NotFoundException.class, () -> service.getItemsBookings(user1.getId(),"ALL", 0, 20));
    }

}
