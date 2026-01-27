package com.example.demo.global.init;

import com.example.demo.domain.post.entity.Post;
import com.example.demo.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor //final 필드를 받는 생성자 자동 생성
public class InitDataHelper {
    private final PostRepository postRepository;

    public Post createPost(String title, String content){
        return postRepository.save(
                Post.create(title,content)
        );
    }


}
