package com.gwu.cs6431.service.user;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserMaintenance {
    private Map<String, User> userMap = new ConcurrentHashMap<>();
    private Set<User> onlineUserSet = new HashSet<>();
}
