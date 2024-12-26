package com.utils.filesIO;
import java.io.IOException;

public interface IFileHandler<T> {
    void write(String fileName, T data) throws IOException;
    T read(String fileName) throws IOException;
}
