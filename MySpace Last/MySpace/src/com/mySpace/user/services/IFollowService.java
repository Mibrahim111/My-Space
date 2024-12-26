package com.mySpace.user.services;

import com.mySpace.user.models.User;

import java.util.ArrayList;

public interface IFollowService {
     void follow(User MyUser , User targetUser);
     void blockFollower(User myUser , User blockedUser);
     void unblockFollower(User my , User blockedUser);
     void YouBothFollow(User myUser , User targetUser);
     void YouBothAreFollowedBy(User myUser , User targetUser);
     void removeFollower(User myUser , User user);
     ArrayList<User> FollowsMeBack(User myUser);
}
