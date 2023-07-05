package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentDto {

    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;

}
