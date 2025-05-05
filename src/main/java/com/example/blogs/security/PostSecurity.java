package com.example.blogs.security;

import com.example.blogs.Services.JpaPostService;
import com.example.blogs.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Security helper bean that provides post-specific security checks
 * Used in @PreAuthorize annotations in PostController
 */
@Component("postSecurity")
public class PostSecurity {

    private final JpaPostService postService;
    
    @Autowired
    public PostSecurity(JpaPostService postService) {
        this.postService = postService;
    }
    
    /**
     * Check if the authenticated user is the author of the post
     * 
     * @param authentication Current authentication object
     * @param postId ID of the post to check
     * @return true if the user is the author of the post
     */
    public boolean isAuthor(Authentication authentication, Long postId) {
        if (authentication == null) {
            return false;
        }
        
        Post post = postService.getPostById(postId);
        if (post == null) {
            return false;
        }
        
        // Check if the authenticated username (email) matches the post author
        // Note: We might need to adjust this based on how posts store authors
        return authentication.getName().equals(post.getAuthor());
    }
}
