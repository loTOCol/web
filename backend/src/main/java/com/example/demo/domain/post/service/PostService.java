package com.example.demo.domain.post.service;

//import com.example.demo.domain.post.dto.request.PostCreateRequest;
//import com.example.demo.domain.post.dto.request.PostUpdateRequest;
import com.example.demo.domain.post.entity.Post;
import com.example.demo.domain.post.repository.PostRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    // 전체 게시글 조회
    public List<Post> findAll(){
        return postRepository.findAll();
    }

    // 단일 게시글 조회
    public Post findById(UUID postId){
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    // 게시글 생성
    @Transactional
    public Post createPost(UUID userId, String title, String content){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다."));

        Post post = Post.create(title, content); // 엔티티의 팩토리 메서드 사용
        return postRepository.save(post); // DB에 저장 후 반환
    }

    // 게시글 수정
    @Transactional
    public void updatePost(UUID postId, UUID userId, String title, String content){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if(!post.isWrittenBy(userId)){
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        post.update(title,content);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(UUID postId, UUID userId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        if(!post.isWrittenBy(userId)){
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        postRepository.deleteById(postId);
    }

}
