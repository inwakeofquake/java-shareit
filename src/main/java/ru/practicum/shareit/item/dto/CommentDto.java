package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentDto {

    private Long id;
    @NotNull
    private String text;
    private String authorName;
    private LocalDateTime created;

}
