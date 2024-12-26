package com.mySpace.user.services;

import com.mySpace.user.models.User;
import com.mySpace.user.repository.UserRepository;

import java.util.List;

public class UserSearchService implements ISearchService<User> {

    private final Trie userTrie = new Trie();
   private final UserRepository userRepository = UserRepository.getInstance();

   public UserSearchService(){
       userRepository.getUserStorage().values().forEach(user -> userTrie.insert(user.getUserName()));
   }

    @Override
    public User searchByKey(String userName) {

        User user = userRepository.findByKey(userName);
        if (user == null) {
            System.err.println("No user found with the username: " + userName);
            return null;
        }
        System.out.println("User found!");
        user.viewUser();
        return user;
    }


    @Override
    public List<String> searchByPrefix(String prefix) {
        return userTrie.getWordsWithPrefix(prefix);
    }


}
