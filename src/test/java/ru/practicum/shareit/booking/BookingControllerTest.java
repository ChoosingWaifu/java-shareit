package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingServiceImpl mockBookingService;
    @Autowired
    private ObjectMapper mapper;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private final User user1 = new User(1L, "user1", "user1@yandex.ru");
    private final User user2 = new User(1L, "user2", "user2@yandex.ru");
    private final BookingPostDto.UserInfo user2Info = new BookingPostDto.UserInfo(user2.getId());
    private final Item item1 = new Item(1L, "item1", "descItem1", true, user1.getId(), null, new ArrayList<>());
    private final BookingPostDto.ItemInfo item1Info = new BookingPostDto.ItemInfo(item1.getId(), item1.getName());
    private final BookingDto bookingDto1 = new BookingDto(LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(100), item1.getId());
    private final BookingPostDto bookingPostDto1 = new BookingPostDto(1L, LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(100), user2Info, item1Info, BookingStatus.WAITING);

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void addBooking() throws Exception {
        when(mockBookingService.post(any(), anyLong()))
                .thenReturn(bookingPostDto1);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user2.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingPostDto1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is((bookingPostDto1.getStart().format(formatter)))))
                .andExpect(jsonPath("$.end", is(bookingPostDto1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.booker.id", is(bookingPostDto1.getBooker().getId()), Long.class));
    }

    @Test
    void update() throws Exception {
        when(mockBookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingPostDto1);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user2.getId())
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingPostDto1.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingPostDto1.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingPostDto1.getItem().getName())));
    }

    @Test
    void getById() throws Exception {
        when(mockBookingService.getDtoById(anyLong(), anyLong()))
                .thenReturn(bookingPostDto1);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingPostDto1.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingPostDto1.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingPostDto1.getItem().getName())));
    }

    @Test
    void getAllUserBookings() throws Exception {
        when(mockBookingService.getUserBookings(eq(user2.getId()), eq("ALL"), anyInt(), anyInt()))
                .thenReturn(List.of(bookingPostDto1));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemsBookings() throws Exception {
        when(mockBookingService.getUserBookings(eq(user1.getId()), eq("ALL"), anyInt(), anyInt()))
                .thenReturn(List.of(bookingPostDto1));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}