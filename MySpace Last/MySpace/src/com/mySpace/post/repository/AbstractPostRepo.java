package com.mySpace.post.repository;

import com.mySpace.post.models.Post;
import com.mySpace.user.repository.IRepository;
import com.utils.filesIO.ArrayListIO;

import java.io.IOException;
import java.util.ArrayList;

public abstract class AbstractPostRepo implements IRepository<Post> {
    protected final ArrayList<Post> postStorage = new ArrayList<>(1000);
    protected final String FILE_NAME = "posts.dat";

    @Override
    public void add(Post post) {

            postStorage.add(post);

    }

    @Override
    public void saveToFile() throws IOException {
        synchronized (postStorage) {
            ArrayListIO<Post> fileHandler = new ArrayListIO<>();
            fileHandler.write(FILE_NAME, postStorage);
        }
    }

    @Override
    public void loadFromFile() throws IOException {
        ArrayListIO<Post> fileHandler = new ArrayListIO<>();
        ArrayList<Post> loadedData = fileHandler.read(FILE_NAME);
        synchronized (postStorage) {
            postStorage.addAll(loadedData); // Merge loaded data into storage
        }
    }

    public ArrayList<Post> getPostStorage() {
        return postStorage;
    }
}
