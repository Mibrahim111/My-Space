package com.mySpace.communities;


import com.mySpace.user.services.ISearchService;
import com.mySpace.user.services.Trie;

import java.util.List;

public class CommunitySearchService implements ISearchService<Community> {

    private final Trie userTrie = new Trie();
    private final CommunitiesRepository communitiesRepository =
            CommunitiesRepository.getInstance();


    public CommunitySearchService(){
        communitiesRepository.getCommunityStorage().values().forEach(community -> userTrie.insert(community.getName()));
    }

    @Override
    public Community searchByKey(String userName) {

        Community community = communitiesRepository.getByKey(userName);
        if (community == null) {

            return null;
        }

        return community;
    }


    @Override
    public List<String> searchByPrefix(String prefix) {
        return userTrie.getWordsWithPrefix(prefix);
    }


}
