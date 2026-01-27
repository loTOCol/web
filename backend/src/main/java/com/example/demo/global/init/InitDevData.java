package com.example.demo.global.init;

import com.example.demo.domain.post.entity.Post;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("dev")
@RequiredArgsConstructor
public class InitDevData {
    private final InitDataHelper helper;

    @PostConstruct
    public void init(){
        Post post1 = helper.createPost("제목1","내용1");
        Post post2 = helper.createPost("제목2","내용2");
        Post post3 = helper.createPost("제목3","내용3");
    }
}
