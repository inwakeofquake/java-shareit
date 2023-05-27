package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {

    @NotNull
    private Long id;
    @NotEmpty
    private String description;
    @NotNull
    private User requestor;
    @NotNull
    private LocalDateTime created;
}

