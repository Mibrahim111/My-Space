package com.mySpace.user.repository;

import com.mySpace.user.models.User;
import com.utils.filesIO.MapIO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractUserRepo implements IRepository<User>{

    protected final Map<String, User> userStorage = new HashMap<>(); // userName -> User
    protected final String FILE_NAME = "users.dat";

    @Override
    public abstract void add(User newObject);

    @Override
    public void saveToFile() throws IOException {
        synchronized (userStorage) {
            MapIO<String, User> fileHandler = new MapIO<>();
            fileHandler.write(FILE_NAME, userStorage);
        }
    }

    @Override
    public void loadFromFile() throws IOException {
        MapIO<String, User> fileHandler = new MapIO<>();
        Map<String, User> loadedData = fileHandler.read(FILE_NAME);
        synchronized (userStorage) {
            userStorage.putAll(loadedData); // Merge loaded data into storage
        }
    }
}
