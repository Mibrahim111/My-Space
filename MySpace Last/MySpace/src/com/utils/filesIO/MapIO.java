package com.utils.filesIO;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MapIO<K,V> implements IFileHandler<Map<K,V>> {
    @Override
    public void write(String fileName, Map<K, V> data) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new IOException("Failed to write Map to file: " + fileName, e);
        }
    }

    @Override
    public Map<K, V> read(String fileName) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Map<K, V>) ois.readObject();
        }  catch (FileNotFoundException e) {

            return new HashMap<>();
        } catch (ClassNotFoundException e) {
            throw new IOException("Class type mismatch while reading file: " + fileName, e);
        } catch (IOException e) {
            throw new IOException("Failed to read Map from file: " + fileName, e);
        }
    }
}
