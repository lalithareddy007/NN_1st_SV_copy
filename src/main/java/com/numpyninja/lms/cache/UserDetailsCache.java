package com.numpyninja.lms.cache;

import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.concurrent.TimeUnit;

public class UserDetailsCache implements UserCache {

    private final Cache userCache;

    public UserDetailsCache() {
        GuavaCacheManager cacheManager = new GuavaCacheManager();
        cacheManager.setCacheBuilder(CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES));

        userCache = cacheManager.getCache("users");
    }

    @Override
    public UserDetails getUserFromCache(String username) {
        ValueWrapper wrapper = userCache.get(username);
        return wrapper != null ? (UserDetails) wrapper.get() : null;
    }

    @Override
    public void putUserInCache(UserDetails user) {
        userCache.put(user.getUsername(), user);
    }

    @Override
    public void removeUserFromCache(String username) {
        userCache.evict(username);
    }
}
