package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestServiceImpl mockItemRequestService;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    private final User user1 = new User(1L,"A","waifu@yandex.ru");
    private final ItemRequest itemRequest1 = new ItemRequest(1L, "desc1", user1.getId(), LocalDateTime.now().plusMinutes(60));

    private final ItemRequestDto itemRequestDto1 = new ItemRequestDto("desc1");

    private final ItemRequestInfoDto itemRequestInfoDto1 = new ItemRequestInfoDto(1L, "desc1", user1.getId(), LocalDateTime.now().plusMinutes(60), new ArrayList<>());

    @Test
    void postRequest() throws Exception {
        when(mockItemRequestService.postRequest(any(), anyLong()))
                .thenReturn(itemRequest1);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest1.getDescription())));
    }

    @Test
    void getRequest() throws Exception {
        when(mockItemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestInfoDto1);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestInfoDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestInfoDto1.getDescription())));
    }

    @Test
    void getUserRequests() throws Exception {
        when(mockItemRequestService.getUserRequests(anyLong()))
                .thenReturn(List.of(itemRequestInfoDto1));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequests() throws Exception {
        when(mockItemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestInfoDto1));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
