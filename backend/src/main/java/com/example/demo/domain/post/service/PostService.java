package com.example.demo.domain.post.service;

//import com.example.demo.domain.post.dto.request.PostCreateRequest;
//import com.example.demo.domain.post.dto.request.PostUpdateRequest;
import com.example.demo.domain.post.entity.Post;
import com.example.demo.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor // final 필드를 생성자로 주입
@Transactional// 기본적으로 조회 트랜잭션(readOnly) 적용
public class PostService {
    private final PostRepository postRepository;

    // 전체 게시글 조회
    public List<Post> findAll(){
        return postRepository.findAll();
    }

    // 단일 게시글 조회
    public Post findById(UUID id){
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    // 게시글 생성
    @Transactional
    public Post createPost(String title, String content){
        Post post = Post.create(title, content); // 엔티티의 팩토리 메서드 사용
        return postRepository.save(post); // DB에 저장 후 반환
    }

    // 게시글 수정
    @Transactional
    public void updatePost(UUID id, String title, String content){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.update(title,content);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(UUID id){
        if(!postRepository.existsById(id)){
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
        postRepository.deleteById(id);
    }

}
