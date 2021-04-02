package com.zzw.chatserver.utils;

import com.zzw.chatserver.pojo.vo.SimpleUser;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SocketIoServerMapUtil {
    public static ConcurrentMap<String, String> clientToUidMap = new ConcurrentHashMap<>();
    public static ConcurrentMap<String, SimpleUser> uidToUserMap = new ConcurrentHashMap<>();

    public static void putUid(String clientId, String uid) {
        clientToUidMap.put(clientId, uid);
    }

    public static void putUser(String uid, SimpleUser simpleUser) {
        uidToUserMap.put(uid, simpleUser);
    }


    public static String getUid(String clientId) {
        return clientToUidMap.get(clientId);
    }

    public static SimpleUser getUser(String uid) {
        return uidToUserMap.get(uid);
    }

    public static void removeUid(String clientId) {
        clientToUidMap.remove(clientId);
    }

    public static void removeUser(String uid) {
        uidToUserMap.remove(uid);
    }

    public static Collection<String> getValuesOfUid() {
        return clientToUidMap.values();
    }

    public static Collection<SimpleUser> getValuesOfUser() {
        return uidToUserMap.values();
    }


    public static ConcurrentMap<String, String> getClientToUidMap() {
        return clientToUidMap;
    }

    public static ConcurrentMap<String, SimpleUser> getUidToUserMap() {
        return uidToUserMap;
    }
}