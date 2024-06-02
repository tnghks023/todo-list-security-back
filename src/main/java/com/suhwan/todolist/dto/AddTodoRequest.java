package com.suhwan.todolist.dto;

import com.suhwan.todolist.domain.Todo;
import com.suhwan.todolist.domain.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddTodoRequest {

    @NotNull
    String todoName;


    public Todo toEntity(String author, User user) {
        return Todo.builder()
                .user(user)
                .author(author)
                .todoName(todoName)
                .build();
    }

}
