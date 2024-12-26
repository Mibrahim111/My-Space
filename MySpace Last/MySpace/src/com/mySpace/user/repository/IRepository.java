package com.mySpace.user.repository;

import java.io.IOException;


public interface IRepository<T> {

     void add(T newObject);

     void saveToFile() throws IOException;

     void loadFromFile() throws IOException ;

}
