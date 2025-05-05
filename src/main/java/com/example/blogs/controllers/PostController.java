package com.example.blogs.controllers;

import com.example.blogs.Services.JpaPostService;
import com.example.blogs.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PostController {
    @Autowired
    private JpaPostService postService;

    @GetMapping("/posts")
    public String getAllPosts(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "3") int size,
                              @RequestParam(required = false) String keyword) {

        List<Post> paginatedPosts;
        int totalPosts;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search with pagination
            paginatedPosts = postService.searchPaginatedPosts(keyword, page, size);
            totalPosts = postService.searchPosts(keyword).size();
        } else {
            // Normal pagination without search
            paginatedPosts = postService.getPaginatedPosts(page, size);
            totalPosts = postService.getPosts().size();
        }

        int totalPages = (int) Math.ceil((double) totalPosts / size);

        model.addAttribute("posts", paginatedPosts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalPosts);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword); // Add the keyword to preserve it between page changes

        return "posts";
    }

    // Show new post form - requires authentication
    @GetMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String showNewPostForm(Model model) {
        Post post = new Post();
        
        // Get current authenticated user's name
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        post.setAuthor(currentUserName);
        
        model.addAttribute("post", post);
        return "post-form";
    }

    // Create a new post - requires authentication
    @PostMapping("/posts")
    @PreAuthorize("isAuthenticated()")
    public String createPost(@ModelAttribute("post") Post post, RedirectAttributes redirectAttributes) {
        // Set author to current user's name
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        post.setAuthor(currentUserName);
        
        postService.createPost(post);
        redirectAttributes.addFlashAttribute("success", "Post created successfully!");
        return "redirect:/posts";
    }

    // Show edit post form - requires authentication
    @GetMapping("/posts/edit/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @postSecurity.isAuthor(authentication, #id))")
    public String showEditPostForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "post-form";
    }

    // Update an existing post - requires authentication
    @PostMapping("/posts/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @postSecurity.isAuthor(authentication, #id))")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") Post post, 
                             RedirectAttributes redirectAttributes) {
        // Get existing post
        Post existingPost = postService.getPostById(id);
        
        // Keep the original author - users shouldn't be able to change the author
        post.setAuthor(existingPost.getAuthor());
        
        postService.updatePost(id, post);
        redirectAttributes.addFlashAttribute("success", "Post updated successfully!");
        return "redirect:/posts";
    }

    // View a post - publicly accessible
    @GetMapping("/posts/view/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "post-view";
    }

    // Delete a post - requires admin or author
    @GetMapping("/posts/delete/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @postSecurity.isAuthor(authentication, #id))")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        postService.deletePost(id);
        redirectAttributes.addFlashAttribute("success", "Post deleted successfully!");
        return "redirect:/posts";
    }
}