package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentAuthorNameDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;


public class CommentMapperTest {

    private final Comment comment = new Comment(null, "text", 1L, 1L, null);

    private final CommentAuthorNameDto commentAuthorName = new CommentAuthorNameDto(null, "text", "authorName", null);

    @Test
    void toCommentTest() {
        Comment result = CommentMapper.toComment(comment.getText(), comment.getItem(), comment.getAuthor());
        Assertions.assertEquals(comment.toString(), result.toString());
    }

    @Test
    void toAuthorNameDtoTest() {
        CommentAuthorNameDto result = CommentMapper.toAuthorNameDto(comment, commentAuthorName.getAuthorName());
        Assertions.assertEquals(result.toString(), commentAuthorName.toString());
    }
}
