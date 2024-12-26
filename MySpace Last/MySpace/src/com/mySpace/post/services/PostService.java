package com.mySpace.post.services;

import com.mySpace.post.models.Comment;

import com.mySpace.post.repository.PostRepository;
import com.mySpace.post.models.Post;
import com.mySpace.user.models.User;


import java.util.List;


public class PostService implements IPostServices{

    private final PostRepository postRepository;
    private static PostService instance; // singleton design pattern
    private PostFactory factory;

    private PostService() {
        this.postRepository = PostRepository.getInstance();
        this.factory = new PostFactory();
    }

    public static PostService getInstance() {
        if (instance == null) {
            synchronized (PostService.class) {
                if (instance == null) {
                    instance = new PostService();
                }
            }
        }
        return instance;
    }


    public Post createPost(String content, User author, boolean isPrivate , List<User> taggedUsers) {

        Post post = factory.createPost(content,author,isPrivate,taggedUsers);
        postRepository.add(post);
        return post;
    }

    public void repost(Post originalPost, User newAuthor) {

        if (originalPost == null) {
            System.out.println("Original post not found!");
            return;
        }

        if (originalPost.getRepostUser().contains(newAuthor)) {
            System.out.println("You already reposted this!");
            return;
        }

        if (!(originalPost.getPrivacyStrategy().allowRepost(originalPost))) {
            System.out.println("Only public posts can be reposted.");
            return;
        }

        //prototype design pattern clone the object
        if(originalPost.hasReposted(newAuthor))
        {
            System.err.println("You already reposted");
        }
        else{
        originalPost.incrementNumOfReposts();
        originalPost.getRepostUser().add(newAuthor);
        }
    }


    public PostRepository getPostRepository() {
        return postRepository;
    }
    

    public void addComment(Post post, String text, User author) {
        if (post == null) {
            System.out.println("Post not found!");
            return;
        }
        Comment comment = new Comment(text, author);
        post.getComments().add(comment);
        System.out.println("Comment added: " + text);
    }

    public void addReply(Comment parentComment, String text, User author) {
        if (parentComment == null) {
            System.out.println("Parent comment not found!");
            return;
        }
        Comment reply = new Comment(text, author);
        parentComment.addReply(reply);
        System.out.println("Reply added: " + text);
    }

    public void addLikeToPost(Post post, User user) {
        if (post == null) {
            System.out.println("Post not found!");
            return;
        }

        if (post.hasLiked(user)) {
            System.out.println("You already liked this post!");
            return;
        }

        post.addLike(user);
        System.out.println(user.getUserName() + " liked the post!");
    }

    public void removeLikeFromPost(Post post, User user) {
        if (post == null) {
            System.out.println("Post not found!");
            return;
        }

        if (!post.hasLiked(user)) {
            System.out.println("You haven't liked this post yet!");
            return;
        }

        post.removeLike(user);
        System.out.println(user.getUserName() + " unliked the post!");
    }





    public void likeComment(Comment comment, User user) {
        if (!comment.hasLiked(user)) {
            comment.addLike(user);
        } else {
            comment.getLikedByUsers().remove(user); // Toggle behavior (optional)
        }
    }

}
