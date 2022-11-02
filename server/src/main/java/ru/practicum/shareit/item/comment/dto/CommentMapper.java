package ru.practicum.shareit.item.comment.dto;

import ru.practicum.shareit.item.comment.Comment;

public class CommentMapper {

    public static Comment toComment(String comment, Long item, Long author) {
        return new Comment(null,
                comment,
                item,
                author,
                null
        );
    }

    public static CommentAuthorNameDto toAuthorNameDto(Comment comment, String authorName) {
        return new CommentAuthorNameDto(
                comment.getId(),
                comment.getText(),
                authorName,
                comment.getCreated()
        );
    }
}
