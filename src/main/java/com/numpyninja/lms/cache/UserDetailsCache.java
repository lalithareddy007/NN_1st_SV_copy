package com.numpyninja.lms.cache;

import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserDetailsCache implements UserCache {
    private final Map<String, UserDetails> cache = new ConcurrentHashMap<>();  //we use a simple in-memory map (cache) to store the UserDetails objects.
    @Override
    public UserDetails getUserFromCache(String username) {
        return cache.get(username);
    }

    @Override
    public void putUserInCache(UserDetails user) {
        cache.put(user.getUsername(), user);
    }

    @Override
    public void removeUserFromCache(String username) {
        cache.remove(username);
    }
}
