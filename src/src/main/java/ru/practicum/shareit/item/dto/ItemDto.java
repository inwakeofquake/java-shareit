package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
        private Long id;
        @NotEmpty
        private String name;
        @NotEmpty
        private String description;
        @NotNull
        private Boolean available;
        private UserDto owner;
        private String request;
}
