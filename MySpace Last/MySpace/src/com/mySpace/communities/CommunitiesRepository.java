package com.mySpace.communities;


import com.mySpace.user.repository.IRepository;
import com.utils.filesIO.MapIO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommunitiesRepository implements IRepository<Community> {

    private Map<String, Community> communityStorage = new HashMap<>();
    private static CommunitiesRepository instance;
    private static final String FILE_NAME = "comms.dat";

    private CommunitiesRepository() {
        try {
            loadFromFile(); // Load data from file at initialization
        } catch (IOException e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }

        // Register shutdown hook to save data before program termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveToFile();
            } catch (IOException e) {
                System.err.println("Failed to save users: " + e.getMessage());
            }
        }));
    }


    public static CommunitiesRepository getInstance() {
        if (instance == null) {


                    instance = new CommunitiesRepository();


        }
        return instance;
    }

    public void add(Community community){
        communityStorage.put(community.getName(), community);
    }

    public Community getByKey(String name){
        return communityStorage.get(name);
    }


    public Community deleteByKey(String name){
        return communityStorage.remove(name);
    }

    public boolean nameExists(String name){
        return communityStorage.containsKey(name);
    }

    public Map<String, Community> getCommunityStorage() {
        return communityStorage;
    }


    @Override
    public void saveToFile() throws IOException {
        synchronized (communityStorage) {
            MapIO<String, Community> fileHandler = new MapIO<>();
            fileHandler.write(FILE_NAME, communityStorage);
        }
    }

    @Override
    public void loadFromFile() throws IOException {
        MapIO<String, Community> fileHandler = new MapIO<>();
        Map<String, Community> loadedData = fileHandler.read(FILE_NAME);
        synchronized (communityStorage) {
            communityStorage.putAll(loadedData); // Merge loaded data into storage
        }
    }
}
