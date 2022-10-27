package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.InsufficientRightsException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.item.model.ItemServiceImpl;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository mockItemRepository;

    private final User user1 = new User(1L,"A","waifu@yandex.ru");
    private final Item item1 = new Item(1L,"item1","desc1",true,user1.getId(),null, new ArrayList<>());

    private final Item itemWrongOwner = new Item(1L,"item1","desc1",true,user1.getId() + 1,null, new ArrayList<>());
    private final Item item1Update = new Item(1L,"itemUpdate","desc1",true,user1.getId(),null, new ArrayList<>());
    private final ItemDto itemDto1 = new ItemDto(1L,"item1","desc1",true,null);

    private final ItemDto itemDtoUpdate1 = new ItemDto(1L,"itemUpdate","desc1",true,null);

    @Test
    void getById() throws Exception {
        Mockito.when(mockItemRepository.findById(any()))
                .thenReturn(Optional.of(item1));
        Assertions.assertEquals(item1.toString(), itemService.getById(item1.getId()).toString());
    }

    @Test
    void searchItem() {
        Mockito.when(mockItemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailable(any(), any(), any(), any()))
                .thenReturn(List.of(item1));
        Assertions.assertEquals(List.of(itemDto1).toString(), itemService.searchItem("ww", 0, 20).toString());
    }

    @Test
    void deleteItem() throws Exception {
        Mockito.when(mockItemRepository.findById(any())).thenReturn(Optional.of(item1));
        itemService.deleteItem(item1.getId(), item1.getOwner());
        Mockito.verify(mockItemRepository, Mockito.times(1)).deleteById(item1.getId());
        Assertions.assertThrows(InsufficientRightsException.class,() -> itemService.deleteItem(item1.getId(), item1.getOwner() + 1));
    }

    @Test
    void updateItem() throws Exception {
        Mockito.when(mockItemRepository.findById(any()))
                .thenReturn(Optional.of(item1));
        Mockito.when(mockItemRepository.save(any()))
                .thenReturn(item1Update);
        Assertions.assertEquals(itemDtoUpdate1.toString(), itemService.updateItem(itemDtoUpdate1, item1.getId(), 1L).toString());
        Mockito.when(mockItemRepository.findById(any()))
                .thenReturn(Optional.of(itemWrongOwner));
        Assertions.assertThrows(InsufficientRightsException.class, () -> itemService.updateItem(itemDtoUpdate1, item1.getId() + 1, 1L));
    }

}
