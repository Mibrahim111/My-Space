package com.mySpace.communities;


import java.util.*;

public class CommunityServices {

   CommunitiesRepository repository = CommunitiesRepository.getInstance();

    public Community createCommunity( String name,
                                   Visibility visibility) {
        if (repository.nameExists(name)) {
           return null;
        }
        Community newCommunity = new Community(name, visibility);
        repository.add(newCommunity);

        return newCommunity;
    }

    public String viewCommunity( String name) {
        Community community = repository.getByKey(name);
        if (community == null) {
            return "Community not found.";
        }
        StringBuilder details = new StringBuilder();
        details.append("Community Name: ").append(community.getName()).append("\n")
                .append("Visibility: ").append(community.getVisibility()).append("\n")
                .append("Members: ").append(community.getMembers().size()).append("\n")
                .append("Posts: ").append(community.getPosts().size()).append("\n");
        return details.toString();
    }

    public Community findCommunityByName(String name) {
        return this.listAllCommunities().stream()
                .filter(community -> community.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<Community> listAllCommunities() {
        return new ArrayList<>(repository.getCommunityStorage().values());
    }


}