package com.utils.filesIO;

import java.io.*;
import java.util.ArrayList;


public class ArrayListIO<T> implements IFileHandler<ArrayList<T>>{
    @Override
    public void write(String fileName, ArrayList<T> data) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(data);
        }catch (IOException e) {
            throw new IOException("Failed to write ArrayList to file: " + fileName, e);
        }
    }

    @Override
    public ArrayList<T> read(String fileName) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (ArrayList<T>) ois.readObject();
        } catch (FileNotFoundException e) {

            return new ArrayList<>();
        } catch (ClassNotFoundException e) {
            throw new IOException("Class type mismatch while reading file: " + fileName, e);
        } catch (IOException e) {
            throw new IOException("Failed to read ArrayList from file: " + fileName, e);
        }
    }
}
