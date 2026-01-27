package com.example.demo.domain.post.controller;

//import com.example.demo.domain.post.dto.request.PostCreateRequest;
//import com.example.demo.domain.post.dto.request.PostUpdateRequest;
import com.example.demo.domain.post.dto.request.PostCreateRequest;
import com.example.demo.domain.post.dto.response.PostResponse;
import com.example.demo.domain.post.entity.Post;
import com.example.demo.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor // final 필드(PostService)를 생성자로 자동 주입
public class PostController {

    private final PostService postService;

    // 전체 게시글 조회
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(){
        List<PostResponse> responses = postService.findAll()
                .stream()
                .map(PostResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // 단일 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable UUID id){
        Post post = postService.findById(id);
        return ResponseEntity.ok(PostResponse.from(post));
    }

    // 게시글 생성
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody @Valid PostCreateRequest request){
        Post post = postService.createPost(request.title(),request.content());
        return ResponseEntity.ok(PostResponse.from(post));
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable UUID id, @RequestBody @Valid PostCreateRequest request){
        postService.updatePost(id, request.title(), request.content());
        return ResponseEntity.noContent().build();
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id){
        postService.deletePost(id);
        return  ResponseEntity.noContent().build();
    }


}

