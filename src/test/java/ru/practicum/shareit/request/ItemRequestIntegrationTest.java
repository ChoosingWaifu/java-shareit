package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:share-it")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestIntegrationTest {

    private ItemRequestService service;
    private UserRepository userRepository;
    private ItemRequestRepository repository;
    private ItemRepository itemRepository;

    private final User user1 = new User(1L,"A","waifu@yandex.ru");
    private final User user2 = new User(2L,"B","waifu2@yandex.ru");
    private final Item item1 = new Item(1L,"item1","desc1",true,1L,1L, new ArrayList<>());
    private final Item item2 = new Item(2L,"item2","desc2",true,2L,2L, new ArrayList<>());
    private final ItemRequest request1 = new ItemRequest(1L, "request1", user1.getId(), LocalDateTime.now());
    private final ItemRequest request2 = new ItemRequest(2L, "request2", user2.getId(), LocalDateTime.now());


    @BeforeEach
    void beforeEach() {
        service = new ItemRequestServiceImpl(userRepository, repository, itemRepository);
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    void postRequest() {
        Assertions.assertThrows(NotFoundException.class, () -> service.postRequest("request1", 666L));
        ItemRequest result = service.postRequest("request1", user1.getId());
        Assertions.assertEquals(user1.getId(), result.getUserId());
        Assertions.assertEquals("request1", result.getDescription());
    }

    @Test
    void getById() {
        Assertions.assertThrows(NotFoundException.class, () -> service.getRequestById(request1.getId(), 666L));
        itemRepository.save(item1);
        repository.save(request1);
        ItemRequestInfoDto result = service.getRequestById(request1.getId(), user1.getId());
        Assertions.assertEquals(request1.getDescription(), result.getDescription());
        Assertions.assertEquals(request1.getUserId(), result.getUserId());
        Assertions.assertEquals(result.getItems().toString(), List.of(ItemMapper.toItemDto(item1)).toString());

        repository.delete(request1);
        Assertions.assertThrows(NotFoundException.class, () -> service.getRequestById(request1.getId(), user1.getId()));
    }

    @Test
    void getAll() {
        repository.save(request1);
        repository.save(request2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        List<ItemRequestInfoDto> result = service.getAllRequests(user1.getId(), 0, 20);
        Assertions.assertEquals(result.get(0).getItems().toString(), List.of(ItemMapper.toItemDto(item2)).toString());
    }

    @Test
    void getUserRequests() {
        Assertions.assertThrows(NotFoundException.class, () -> service.getUserRequests(666L));
        repository.save(request1);
        repository.save(request2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        List<ItemRequestInfoDto> result = service.getUserRequests(user1.getId());
        Assertions.assertEquals(result.get(0).getItems().toString(), List.of(ItemMapper.toItemDto(item1)).toString());
    }

}
