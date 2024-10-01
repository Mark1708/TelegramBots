package org.telegram.telegrambots.starter.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHolder<T> {
    private Integer updateId;
    private Update origin;
    private T update;
    private String type;
    private User user;
    private String text;

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    public Optional<String> getText() {
        return Optional.ofNullable(text);
    }
}
