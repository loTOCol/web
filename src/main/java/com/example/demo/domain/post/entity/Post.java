package com.example.demo.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
//@Setter 엔티티에서 지양
//@AllArgsConstructor 지양
@NoArgsConstructor(access = AccessLevel.PROTECTED) //기본 생성자 보호
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "post_id", columnDefinition = "BINARY(16)")  // UUID를 MySQL/H2에서 저장할 타입 지정
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    //팩토리 메서드만 허용
    private Post (String title, String content){
        this.title = title;
        this.content = content;
    }

    //아직 객체가 없는 상태에서 호출되므로 static이어야 함
    public static Post create(String title, String content){
        validate(title,content);

        return new Post(title,content);
    }

    public void update(String title, String content){
        validate(title,content);

        this.title = title;
        this.content = content;
    }

    private static void validate(String title, String content){
            if(title == null || title.isBlank()){
                throw new IllegalArgumentException("제목은 필수입니다.");
            }

            if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }
    }

}
