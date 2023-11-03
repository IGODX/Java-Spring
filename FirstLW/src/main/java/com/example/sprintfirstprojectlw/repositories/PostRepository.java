package com.example.sprintfirstprojectlw.repositories;

import com.example.sprintfirstprojectlw.models.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
}
