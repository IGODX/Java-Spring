package com.example.sprintfirstprojectlw.models.controllers;

import com.example.sprintfirstprojectlw.models.Post;
import com.example.sprintfirstprojectlw.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BlogController {
    @Autowired
    private PostRepository postRepository;


@GetMapping("/blog")
public String getBlog(@RequestParam(required = false) String header, Model model){
Iterable<Post> iterable = postRepository.findAll();
    List<Post> posts = new ArrayList<>();
    iterable.forEach(posts::add);
    if(header != null && !header.isEmpty())
        posts = posts.stream().filter(e-> e.getHeader().toLowerCase().contains(header.toLowerCase())).collect(Collectors.toList());
    model.addAttribute("posts", posts);
return "blog";
}
    @GetMapping("/blog/newPost")
    public String getNewPost(Model model){
        return "newPost";
    }

    @PostMapping("/blog/{id}/remove")
public String postRemove(@PathVariable(value = "id") long id, Model model){
        Post post = postRepository.findById(id).orElse(null);
       assert post != null;
        deleteFile( "src/main/resources/static/" + post.getLinkedImage());
        postRepository.delete(post);
        return "redirect:/blog";
    }
    @GetMapping("/blog/{id}/remove")
    public String getRemove(@PathVariable(value = "id") long id, Model model){
        Post post = postRepository.findById(id).orElse(null);
        assert post != null;
        model.addAttribute("post",post);
        return "delete";
    }

    @GetMapping("/blog/{id}/edit")
    public String getEdit(@PathVariable(value = "id") long id, Model model){
        Post post = postRepository.findById(id).orElse(null);
        if(post != null)
        model.addAttribute("post",post);
        return "edit";
    }
    @PostMapping("/blog/{id}/edit")
    public String postEdit(@PathVariable(value = "id") long id,
                           @RequestParam String context,
                           @RequestParam String header,
                           @RequestParam MultipartFile linkImage) {
        Post post = postRepository.findById(id).orElse(null);
        assert post != null;
        if (!context.equals(post.getContext()))
            post.setContext(context);
        if (!header.equals(post.getHeader()))
            post.setHeader(header);
        if (linkImage != null) {
            deleteFile( "src/main/resources/static/" + post.getLinkedImage());
                String uploadDirectory = "src/main/resources/static/images/" + linkImage.getOriginalFilename();
                insertFile(linkImage, uploadDirectory);
                String fPath = "/images/" + linkImage.getOriginalFilename();
                post.setLinkedImage(fPath);
                postRepository.save(post);
        }
        return "redirect:/blog";
    }
    @PostMapping("/blog/create")
    public String getNewPostToPost(
            @RequestParam String context,
            @RequestParam String header,
            @RequestParam MultipartFile linkedImage,
            Model model
    )
    {
        Post post = new Post();
        post.setContext(context);
         post.setHeader(header);
        String uploadDirectory = "src/main/resources/static/images/" + linkedImage.getOriginalFilename();
        insertFile(linkedImage, uploadDirectory);
        String filePath = "images/" + linkedImage.getOriginalFilename();
        post.setLinkedImage(filePath);
        postRepository.save(post);
            return "redirect:/blog";
    }

    private void insertFile(MultipartFile linkedImage, String uploadDirectory){
        try (var fileInputStream = linkedImage.getInputStream();
             FileOutputStream output = new FileOutputStream(uploadDirectory)
        ) {
            output.write(fileInputStream.readAllBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteFile(String filePath){
        Path path = Paths.get(filePath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
