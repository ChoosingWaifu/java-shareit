package ru.practicum.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class CommentAuthorNameDto {

    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
