package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @EqualsAndHashCode.Include
    private String name;

    @Email
    @NotBlank
    @EqualsAndHashCode.Include
    private String email;
}
