package com.example.myproject.Controller;

import com.example.myproject.ConstantMessage.ResponeMessage;
import com.example.myproject.Service.CommentService;
import com.example.myproject.Service.PostService;
import com.example.myproject.Service.UserService;
import com.example.myproject.db.Comment;
import com.example.myproject.db.Post;
import com.example.myproject.db.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@Slf4j
//@RequestMapping(value = "/users")
public class MainController {
    private UserService userService;
    private PostService postService;
    private CommentService commentService;

    @Autowired
    public MainController(UserService userService, PostService postService, CommentService commentService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping("/")
    public String showLogin(Model model) {
        model.addAttribute("user", new User());
        Object message = model.asMap().get("message");
        if (message != null) {
            model.addAttribute("message", message.toString());
        }

        return "index";
    }

    @GetMapping("/users/new")
    public String showAddUser(Model model) {
        model.addAttribute("user", new User());
        return "add_users_form";
    }

    @PostMapping("/users/add")
    public String addUser(User user, RedirectAttributes ra, HttpSession session) throws Exception {
        User currUser = (User) session.getAttribute("user");
        if (userService.findUserByEmail(user.getUserEmail()) != null) {
            ra.addFlashAttribute("message", ResponeMessage.duplicateEmailError);
            return "redirect:/users/new";
        } else {
            boolean check = userService.addUser(user);
            if (!check) {
                ra.addFlashAttribute("message", ResponeMessage.addUserError);
                return "redirect:/users/new";
            } else {
                if (currUser != null) {
                    User admin = userService.loginUser(currUser);
                    if (Objects.equals(admin.getUserRole(), "admin")) {
                        ra.addFlashAttribute("message", ResponeMessage.addUserSuccess);
                        return "redirect:/admin?id=" + admin.getUserId();
                    }
                }
                ra.addFlashAttribute("message", ResponeMessage.addUserSuccess);
                return "redirect:/";
            }
        }

    }

    @PostMapping("/users/login")
    public String loginUser(User user, HttpSession session, RedirectAttributes ra) throws Exception {
        User checkUser = userService.loginUser(user);
        if (checkUser != null) {
            session.setAttribute("user", user);
            return "redirect:/forums";
        } else {
            ra.addFlashAttribute("message", ResponeMessage.loginError);
            return "redirect:/";
        }
    }

    @GetMapping("/forums")
    public String showForum(Model model, HttpSession session, RedirectAttributes ra) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        List<Post> topViewedPosts = postService.listTopViewedPost();
        if (topViewedPosts.size() != 0) {
            for (Post post : topViewedPosts) {
                String posterName = (userService.findUserById(post.getPostBy())).getUserName();
                post.setPostByName(posterName);
            }
        }
        List<Post> mostRecentPosts = postService.listMostRecentPost();
        if (mostRecentPosts.size() != 0) {
            for (Post post : mostRecentPosts) {
                String posterName = (userService.findUserById(post.getPostBy())).getUserName();
                post.setPostByName(posterName);
            }
        }
        User currUser = userService.loginUser(user);
        model.addAttribute("topViewedPosts", topViewedPosts);
        model.addAttribute("mostRecentPosts", mostRecentPosts);
        model.addAttribute("id", currUser.getUserId());
        model.addAttribute("currUserName", currUser.getUserName());
        model.addAttribute("currUserRole", currUser.getUserRole());
        return "forums";
    }

    @GetMapping("/posts")
    public String showPost(@RequestParam("keyword") String keyword,@RequestParam(value = "page", defaultValue = "1") int page, Model model, HttpSession session, RedirectAttributes ra) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        User currUser = userService.loginUser(user);
        model.addAttribute("id", currUser.getUserId());
        model.addAttribute("currUserName", currUser.getUserName());
        model.addAttribute("currUserRole", currUser.getUserRole());
        String cleanedKeyword = keyword.replaceAll("/page=" + page, "");
        List<Post> posts = postService.listPostBySubject(cleanedKeyword);
        model.addAttribute("noPage", (int) Math.ceil((double) posts.size() / 10));
        int pageSize = 10;
        int startIndex = (page - 1) * pageSize;
        int endIndex = startIndex + pageSize;
        List<Post> postsOnPage = posts.subList(startIndex, Math.min(endIndex, posts.size()));
        for (Post post : postsOnPage) {
            String posterName = (userService.findUserById(post.getPostBy())).getUserName();
            post.setPostByName(posterName);
        }
        model.addAttribute("posts", postsOnPage);
        model.addAttribute("keyword", cleanedKeyword);
        model.addAttribute("currPage", page);
        return "posts";
    }

    @PostMapping("/details/submit")
    public String handleReply(@RequestParam("reply") String replyContent, @RequestParam("commentTo") String commentTo, @RequestParam("commentToId") int id, HttpSession session, RedirectAttributes ra) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }

        User currUser = userService.loginUser(user);
        Comment comment = new Comment(1, id, commentTo, currUser.getUserId(), replyContent, null, null, null,null);
        if (commentService.addComment(comment)) {
            List<Comment> comments = commentService.listCommentByPost(id);
            postService.updatePostReplies(id, comments.size() - 1);
        }
        return "redirect:/details?id="+id;

    }

    @GetMapping("/details")
    public String getDetails(@RequestParam("id") String id, @RequestParam(value = "page", defaultValue = "1") int page, Model model, HttpSession session, RedirectAttributes ra) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        String cleanedId = id.replaceAll("/page=" + page, "");
        List<Comment> comments = commentService.listCommentByPost(Integer.parseInt(cleanedId));
        for (Comment comment : comments) {
            User userCommentToName = (userService.findUserById(comment.getCommentTo()));
            User userCommentByName = (userService.findUserById(comment.getCommentBy()));
            if (userCommentToName != null) {
                comment.setUserCommentToName(userCommentToName.getUserName());
                comment.setUserCommentByName(userCommentByName.getUserName());
            } else {
                comment.setUserCommentToName("not reply");
                comment.setUserCommentByName(userCommentByName.getUserName());
            }
            comment.setUserCommentRole(userCommentByName.getUserRole());

        }
        model.addAttribute("poster", comments.get(0));

        model.addAttribute("noPage", (int) Math.ceil((double) comments.size() / 10));
        int pageSize = 10;
        int startIndex = (page - 1) * pageSize;
        int endIndex = startIndex + pageSize;
        List<Comment> commentsOnPage = comments.subList(startIndex, Math.min(endIndex, comments.size()));
        if (page == 1) {
            commentsOnPage.remove(0);
        }
        User currUser = userService.loginUser(user);
        model.addAttribute("id", currUser.getUserId());
        model.addAttribute("currUserName", currUser.getUserName());
        model.addAttribute("comments", commentsOnPage);
        model.addAttribute("postId", id);
        model.addAttribute("currPage", page);
        model.addAttribute("currUserRole", currUser.getUserRole());
        return "details";

    }

    @GetMapping("/user")
    public String showUser(@RequestParam("id") String id, Model model, HttpSession session, RedirectAttributes ra) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        User currUser = userService.loginUser(user);
        User userShown = userService.findUserById(id);

        model.addAttribute("id", id);
        model.addAttribute("userShown", userShown);
        model.addAttribute("currUser", currUser);
        return "user_info";
    }

    @PostMapping("/update/user")
    public String updateUser(@RequestParam("id") String id, RedirectAttributes ra, Model model, HttpSession session) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        User currUser = userService.loginUser(user);
        User userShown = userService.findUserById(id);
        String day = null;
        String month = null;
        String year = null;
        if (!Objects.equals(currUser.getUserId(), userShown.getUserId()) && !Objects.equals(currUser.getUserRole(), "admin")) {
            ra.addFlashAttribute("message", ResponeMessage.permissionDenial);
            return "redirect:/user?id=" + currUser.getUserId();
        }
        if (userShown.getUserDob() != null) {
            String[] dobParts = userShown.getUserDob().split("-");
            day = dobParts[2];
            month = dobParts[1];
            year = dobParts[0];
        }
        model.addAttribute("id", id);
        model.addAttribute("userShown", userShown);
        model.addAttribute("defaultDay", day);
        model.addAttribute("defaultMonth", month);
        model.addAttribute("defaultYear", year);
        model.addAttribute("currUserRole",currUser.getUserRole());
        model.addAttribute("currUserName",currUser.getUserName());
        return "update_user_form";
    }

    @PostMapping("/update/save/user")
    public String saveUpdateUser(@RequestParam("id") String id, @RequestParam("userInfoInput") String userInfo, @RequestParam("dob_day") String editedDay, @RequestParam("dob_month") String editedMonth, @RequestParam("dob_year") String editedYear, User editedUser, RedirectAttributes ra, Model model, HttpSession session) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        User currUser = userService.loginUser(user);
        if (!Objects.equals(currUser.getUserId(), id) && !Objects.equals(currUser.getUserRole(), "admin")) {
            ra.addFlashAttribute("message", ResponeMessage.permissionDenial);
            return "redirect:/user?id=" + currUser.getUserId();
        }
        String day = null;
        String month = null;
        String year = null;
        if (currUser.getUserDob() != null) {
            String[] dobParts = currUser.getUserDob().split("-");
            day = dobParts[2];
            month = dobParts[1];
            year = dobParts[0];
        }
        model.addAttribute("id", id);
        model.addAttribute("defaultDay", day);
        model.addAttribute("defaultMonth", month);
        model.addAttribute("defaultYear", year);
        model.addAttribute("currUser", currUser);
        String editedDob = editedYear + "-" + editedMonth + "-" + editedDay;
        if (!StringUtils.hasText(editedDob)) {
            ra.addFlashAttribute("message", ResponeMessage.nullYearError);
            return "redirect:/user?id=" + id;
        }
        if (!Objects.equals(currUser.getUserRole(), "admin")) {
            if (!Objects.equals(currUser.getUserEmail(), editedUser.getUserEmail())) {
                if (userService.findUserByEmail(editedUser.getUserEmail()) != null) {
                    ra.addFlashAttribute("message", ResponeMessage.duplicateEmailError);
                    return "redirect:/user?id=" + id;
                }
            }
        }
        User updateUser = new User(id, editedUser.getUserName(), editedUser.getUserPassword(), editedUser.getUserEmail(), editedDob, editedUser.getUserLocation(), userInfo, null);
        userService.updateUser(updateUser);
        if (!Objects.equals(editedUser.getUserEmail(), user.getUserEmail())) {
            user.setUserEmail(editedUser.getUserEmail());
        }
        if (!Objects.equals(editedUser.getUserPassword(), user.getUserPassword())) {
            user.setUserPassword(editedUser.getUserPassword());
        }
        session.setAttribute("user", user);
        ra.addFlashAttribute("message", ResponeMessage.updateUserSuccess);
        return "redirect:/user?id=" + id;
    }

    @GetMapping("/posts/create")
    public String showPostUpdateForm(Model model, HttpSession session, RedirectAttributes ra) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        User currUser = userService.loginUser(user);
        model.addAttribute("post", new Post());
        model.addAttribute("currUserRole",currUser.getUserRole());
        model.addAttribute("id",currUser.getUserId());
        return "post_making_form";
    }

    @PostMapping("/posts/create/save")
    public String savePostCreateForm(Post post, Model model, HttpSession session, RedirectAttributes ra) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        User currUser = userService.loginUser(user);
        if (postService.findPostBySubjectOrId(post.getPostSubject(),currUser.getUserId(),null) != null) {
            ra.addFlashAttribute("message", ResponeMessage.duplicateSubjectError);
            return "redirect:/posts/create";
        }
        System.out.println(postService.countPost());
        Post uploadPost = new Post(postService.countPost()+1, post.getPostSubject(), post.getPostContent(), currUser.getUserId(), null, 0, null);
        Comment firstComment = new Comment(0, postService.countPost()+1, "0", currUser.getUserId(), post.getPostContent(), null, null, null,null);
        postService.addPost(uploadPost);
        commentService.addComment(firstComment);
        ra.addFlashAttribute("message", ResponeMessage.addPostSuccess);
        return "redirect:/posts?keyword=";
    }

    @GetMapping("/posts/user")
    public String showUserPost(@RequestParam("id") String id, Model model, HttpSession session, RedirectAttributes ra) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        User currUser = userService.loginUser(user);
        List<Post> posts = postService.listPostByUser(id);
        for (Post post : posts) {
            String posterName = (userService.findUserById(post.getPostBy())).getUserName();
            post.setPostByName(posterName);
        }
        // Đưa danh sách bài viết vào model để hiển thị trên post.html
        model.addAttribute("posts", posts);
        model.addAttribute("id", id);
        model.addAttribute("currUserRole",currUser.getUserRole());
        model.addAttribute("currUserName",currUser.getUserName());
        return "user_post";
    }

    @GetMapping("/edit/details")
    public String updatePost(@RequestParam("id") String postId, Model model, HttpSession session, RedirectAttributes ra) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }

        User currUser = userService.loginUser(user);
        Post post = postService.findPostBySubjectOrId(null,currUser.getUserId(),postId);
        model.addAttribute("post", post);
        model.addAttribute("postId", post.getPostId());
        model.addAttribute("currUserRole",currUser.getUserRole());
        model.addAttribute("id",currUser.getUserId());
        return "edit_post_form";
    }

    @PostMapping("/edit/save/details")
    public String savePostUpdateForm(@RequestParam("id") String postId, Post post, Model model, HttpSession session, RedirectAttributes ra) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        User currUser = userService.loginUser(user);
        String oldSubject = (postService.findPostBySubjectOrId(null, currUser.getUserId(),postId)).getPostSubject();
        if(!Objects.equals(post.getPostSubject(),oldSubject)) {
            if (postService.findPostBySubjectOrId(post.getPostSubject(), currUser.getUserId(), null) != null) {
                ra.addFlashAttribute("message", ResponeMessage.duplicateSubjectError);
                return "redirect:/edit/details?id=" + postId;
            }
        }
        Post uploadPost = new Post(post.getPostId(), post.getPostSubject(), post.getPostContent(), post.getPostBy(), post.getPostDate(), post.getPostReplies(), post.getPostByName());
        postService.updatePostById(uploadPost);
        commentService.updateCommentOfPost(post.getPostContent(),post.getPostId());
        ra.addFlashAttribute("message", ResponeMessage.addPostSuccess);
        return "redirect:/details?id=" + postId;
    }
    @GetMapping("delete/details")
    public String deletePostById(@RequestParam("id") String id,  Model model, HttpSession session, RedirectAttributes ra) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        postService.deletePost(Integer.parseInt(id));
        commentService.deleteByPost(Integer.parseInt(id));
        return "redirect:/posts?keyword=";
    }
    @GetMapping("/admin")
    public String showListUser(@RequestParam("id") String id,Model model, HttpSession session, RedirectAttributes ra) throws Exception{
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        User currUser = userService.loginUser(user);
        if(!Objects.equals(currUser.getUserRole(),"admin")){
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/user?id=" + id;
        }
        List<User> allUser = userService.listAllUser();
        model.addAttribute("id",id);
        model.addAttribute("currUserName",currUser.getUserName());
        model.addAttribute("userList",allUser);
        return "admin_management";
    }
    @GetMapping("/delete/user")
    public String deleteUser(@RequestParam("id") String id,Model model, HttpSession session, RedirectAttributes ra) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        User currUser = userService.loginUser(user);
        if(!Objects.equals(currUser.getUserRole(),"admin")){
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/user?id=" + id;
        }
        userService.deleteUser(id);
        List<Post> userPosts = postService.listPostByUser(id);
        for (Post post: userPosts){
            postService.deletePost(post.getPostId());
            commentService.deleteByPost(post.getPostId());
        }
        commentService.deleteByUser(id);
        if(Objects.equals(currUser.getUserId(),id)){
            ra.addFlashAttribute("message",ResponeMessage.deleteCurrUserSuccess);
            return "redirect:/";
        }
        ra.addFlashAttribute("message",ResponeMessage.deleteUserSuccess);
        return "redirect:/admin?id=" + currUser.getUserId();
    }
    @GetMapping("/reset")
    public String showResetPassword(Model model) {
        model.addAttribute("user", new User());
        return "reset_password_form";
    }

    @PostMapping("/reset/save")
    public String resetPassword(User user,  RedirectAttributes ra) throws Exception {
        String check = userService.resetPassword(user.getUserName(),user.getUserEmail());

        if(check == null){
            ra.addFlashAttribute("message",ResponeMessage.userNotFound);

        }
        else{
        ra.addFlashAttribute("message","Your new password is: " + check);}
        return "redirect:/";
    }
    @GetMapping("/logout")
    public String logoutUser(HttpSession session, RedirectAttributes ra){
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("message", ResponeMessage.authenticateError);
            return "redirect:/";
        }
        session.invalidate();
        return "redirect:/";
    }
}