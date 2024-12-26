package com.mySpace.user.services;

import java.util.List;

public interface ISearchService<T> {
    T searchByKey(String userName);
    List<String> searchByPrefix(String prefix);

}
