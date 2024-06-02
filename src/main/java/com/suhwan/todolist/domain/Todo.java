package com.suhwan.todolist.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Table(name = "todos")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean completed;

    @Column(nullable = false)
    private String todoName;

    @Column(name = "author", nullable = false)
    private String author;

    @Builder
    public Todo(User user, String todoName, String author) {
        this.user = user;
        this.todoName = todoName;
        this.author = author;
    }

    @ManyToOne
    @JsonBackReference
    private User user;
}
