package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NullStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.item.model.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:share-it")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ItemIntegrationTest {

    private ItemService service;
    private ItemRepository repository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;

    private final User user1 = new User(1L,"A","waifu@yandex.ru");

    private final User user2 = new User(2L,"B","waifu2@yandex.ru");

    private final Item item1 = new Item(1L,"item1","desc1",true,1L,null, new ArrayList<>());

    @BeforeEach
    void beforeEach() {
        service = new ItemServiceImpl(repository, bookingRepository, commentRepository, userRepository);
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    void addNewItem() throws Exception {
        Item result = service.addNewItem(item1, user1.getId());
        Assertions.assertEquals(item1.getName(), result.getName());
        Assertions.assertEquals(item1.getDescription(), result.getDescription());
        Assertions.assertEquals(item1.getAvailable(), result.getAvailable());
        Assertions.assertEquals(item1.getOwner(), result.getOwner());

        Item itemWrongStatus = new Item(1L,"item1","desc1",null,1L,null, new ArrayList<>());
        Assertions.assertThrows(NullStatusException.class, () -> service.addNewItem(itemWrongStatus, user1.getId()));
        Assertions.assertThrows(NotFoundException.class, () -> service.addNewItem(item1, 666L));
    }

    @Test
    void getByIdWithBooking() throws Exception {
        Assertions.assertThrows(NotFoundException.class, () -> service.getByIdWithBooking(item1.getId(), user2.getId()));

        service.addNewItem(item1, user1.getId());
        ItemWithBookingDto result = service.getByIdWithBooking(item1.getId(), user2.getId());
        Assertions.assertEquals(item1.getName(), result.getName());
        Assertions.assertEquals(item1.getDescription(), result.getDescription());
        Assertions.assertEquals(item1.getAvailable(), result.getAvailable());
        Assertions.assertEquals(result.getComments(), new ArrayList<>());

        Assertions.assertThrows(NotFoundException.class, () -> service.addNewItem(item1, 666L));
    }

    @Test
    void getItems() throws Exception {
        repository.save(item1);
        List<ItemWithBookingDto> result = service.getItems(user1.getId(), 0, 20);

        Assertions.assertEquals(item1.getName(), result.get(0).getName());
        Assertions.assertEquals(item1.getDescription(), result.get(0).getDescription());
        Assertions.assertEquals(item1.getAvailable(), result.get(0).getAvailable());
        Assertions.assertEquals(result.get(0).getComments(), new ArrayList<>());
    }

    @Test
    void postComment() throws Exception {
        Assertions.assertThrows(NotFoundException.class,
                () -> service.postComment("comment text", item1.getId(), 666L));
        Assertions.assertThrows(ValidationException.class,
                () -> service.postComment("comment text", item1.getId(), user2.getId()));
        service.addNewItem(item1, user1.getId());
        bookingRepository.save(new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(100),
                item1.getId(), user2.getId(), BookingStatus.APPROVED));
        service.postComment("comment text", item1.getId(), user2.getId());
        ItemWithBookingDto result = service.getByIdWithBooking(item1.getId(), user2.getId());
        Assertions.assertEquals("comment text", result.getComments().get(0).getText());

    }
}
