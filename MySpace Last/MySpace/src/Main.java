import com.mySpace.communities.CommunitiesRepository;
import com.mySpace.post.models.Post;
import com.mySpace.user.models.User;
import com.mySpace.post.services.PostService;
import com.mySpace.user.repository.UserRepository;


public class Main {
    public static void main(String[] args) {


        PostService postService = PostService.getInstance();
        UserRepository userRepository = UserRepository.getInstance();
        CommunitiesRepository communitiesRepository = CommunitiesRepository.getInstance();
        for(User user :userRepository.getUserStorage().values()) {
            System.out.println(user.getUserName());
            user.PrintUserINFO();
            System.out.println();
            System.out.println("======================");

        }

        for (Post post : postService.getPostRepository().getPostStorage() )
        {
            System.out.println("============================================");
            post.displayPost();
            post.getComments().toString();
        }

//        communitiesRepository.getCommunityStorage().clear();

//
//       Post post = postService.createPost("Older Post ",userRepository.findByKey("mohamed"),false,
//               new ArrayList<>());
//        post.setTimestamp(LocalDate.ofEpochDay(2020-11-12));


//        Post post2 = postService.createPost("bourbonibra ",userService.getUserRepository().findByKey("ibra"),false,
//                new ArrayList<>());
//        post2.setTimestamp(LocalDate.ofEpochDay(2024-12-12));

//
//         postService.getPostRepository().getPostStorage().clear();
//        userRepository.getUserStorage().clear();

    }
}

