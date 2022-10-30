package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Valid
@Entity
@Table(name = "requests")
@AllArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String description;
    @NotNull
    @Column(name = "author")
    private Long userId;
    @CreationTimestamp
    private LocalDateTime created;

    @Override
    public String toString() {
        return "ItemRequest{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", userId=" + userId +
                ", created=" + created +
                '}';
    }
}
