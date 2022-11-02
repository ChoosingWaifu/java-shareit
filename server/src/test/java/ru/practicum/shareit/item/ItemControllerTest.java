package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.item.comment.dto.CommentAuthorNameDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemController;
import ru.practicum.shareit.item.model.ItemServiceImpl;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemServiceImpl mockItemService;
    @Autowired
    private ObjectMapper mapper;

    private final User user1 = new User(1L,"A","waifu@yandex.ru");
    private final Item item1 = new Item(1L,"item1","desc1",true,1L,null, new ArrayList<>());
    private final ItemDto itemDto1 = new ItemDto(1L,"itemUpdate","desc1",true,null);
    private final ItemWithBookingDto itemWithBookingDto1 = new ItemWithBookingDto(
            1L,
            "item1",
            "desc1",
            true,
            null,
            null,
            new ArrayList<>());
    private final ItemWithBookingDto itemWithBookingDto2 = new ItemWithBookingDto(
            2L,
            "item2",
            "desc2",
            true,
            null,
            null,
            new ArrayList<>());
    private final CommentDto comment = new CommentDto("comment");
    private final CommentAuthorNameDto commentDto = new CommentAuthorNameDto(
            1L,
            "comment",
            user1.getName(),
            LocalDateTime.now());

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void addItem() throws Exception {
        when(mockItemService.addNewItem(any(), anyLong()))
                .thenReturn(item1);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.available", is(item1.getAvailable())));
    }

    @Test
    void getItemById() throws Exception {
        when(mockItemService.getByIdWithBooking(anyLong(), anyLong()))
                .thenReturn(itemWithBookingDto1);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemWithBookingDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemWithBookingDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithBookingDto1.getAvailable())));
    }

    @Test
    void getAll() throws Exception {
        when(mockItemService.getItems(anyLong(), any(),any()))
                .thenReturn(List.of(itemWithBookingDto1, itemWithBookingDto2));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateItem() throws Exception {
        when(mockItemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(itemDto1);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable())));
    }

    @Test
    void search() throws Exception {
        when(mockItemService.searchItem("desc", 0,
                20))
                .thenReturn(List.of(itemDto1));

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .param("text", "desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void postComment() throws Exception {
        when(mockItemService.postComment(any(), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(comment.getText())));
    }

    @Test
    void deleteItemById() throws Exception {
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk());
    }

}
