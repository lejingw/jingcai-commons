package com.jingcai.apps.common.jdbc.redis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis基本命令封装接口
 *
 * @author Zhang Xu
 */
public interface RedisOperation {

    /**
     * get
     */
    String get(String key) throws Exception;
    Object getObject(String key) throws Exception;

    /**
     * set with expiration
     */
    boolean set(String key, String value, int expiration) throws Exception;
    boolean setObject(String key, Object value, int expiration) throws Exception;

    /**
     * set with no expiration
     */
    boolean set(String key, String value) throws Exception;
    boolean setObject(String key, Object value) throws Exception;

    /**
     * add with expiration
     */
    boolean add(String key, String value, int expiration) throws Exception;
    boolean addObject(String key, Object value, int expiration) throws Exception;

    /**
     * add by leveraging setnx
     */
    boolean add(String key, String valueint) throws Exception;
    boolean addObject(String key, Object value) throws Exception;

    /**
     * exists
     */
    boolean exists(String key) throws Exception;
    boolean existsObject(String key) throws Exception;

    /**
     * delete
     */
    boolean delete(String key);
    boolean deleteObject(String key);

    /**
     * expire
     */
    boolean expire(String key, int seconds);
    boolean expireObject(String key, int seconds);

    /**
     * hash put
     */
    void hput(String key, String field, String fieldValue) throws Exception;
    void hputObject(String key, String field, Object fieldValue) throws Exception;

    /**
     * hash get
     */
    String hget(String key, String field);
    Object hgetObject(String key, String field);

    /**
     * hash del
     */
    boolean hdel(String key, String field) throws Exception;
    boolean hdelObject(String key, String field) throws Exception;

    /**
     * hash keys
     */
    Set<String> hKeys(String key) throws Exception;
    Set<String> hKeysObject(String key) throws Exception;

    /**
     * hash values
     */
    List<String> hValues(String key) throws Exception;
    List<Object> hValuesObject(String key) throws Exception;

    /**
     * hash exsits
     */
    boolean hExists(String key, String field) throws Exception;
    boolean hExistsObject(String key, String field) throws Exception;

    /**
     * hash length
     */
    long hLen(String key) throws Exception;
    long hLenObject(String key) throws Exception;

    /**
     * hash get all
     */
    Map<String, String> hGetAll(String key) throws Exception;
    Map<String, Object> hGetAllObject(String key) throws Exception;

    /**
     * hash multiple set
     */
    void hmSet(String key, Map<String, Serializable> values) throws Exception;

    /**
     * hash multiple get
     */
    List<Object> hmGet(String key, String... fields) throws Exception;

    /**
     * hash multiple get by using basic string serializer
     */
    List<String> hmGetByStringSerializer(String key, String... fields) throws Exception;

    /**
     * hash multiple set by using basic string serializer
     */
    void hmSetByStringSerializer(String key, Map<String, String> values) throws Exception;

    /**
     * set add
     */
    boolean sAdd(String key, String member) throws Exception;

    /**
     * set remove
     *
     * @param key
     * @param member
     *
     * @return
     *
     * @throws Exception
     */
    boolean sRem(String key, String member) throws Exception;

    /**
     * set members
     *
     * @param key
     *
     * @return
     *
     * @throws Exception
     */
    Set<String> sMembers(String key) throws Exception;

    /**
     * list push
     *
     * @param key
     * @param value
     *
     * @return
     *
     * @throws Exception
     */
    boolean lpush(String key, Object value) throws Exception;

    /**
     * list pop
     *
     * @param key
     * @param cls
     *
     * @return
     *
     * @throws Exception
     */
    Object lpop(String key, Class<?> cls) throws Exception;

    /**
     * reverse list push
     *
     * @param key
     * @param value
     *
     * @return
     *
     * @throws Exception
     */
    boolean rpush(String key, Object value) throws Exception;

    /**
     * reverse list pop
     *
     * @param key
     * @param cls
     *
     * @return
     *
     * @throws Exception
     */
    Object rpop(String key, Class<?> cls) throws Exception;

    /**
     * incr
     *
     * @param key
     *
     * @return
     *
     * @throws Exception
     */
    Long incr(String key) throws Exception;

    /**
     * incrBy
     *
     * @param key
     * @param integer
     *
     * @return
     *
     * @throws Exception
     */
    Long incrBy(final String key, final long integer) throws Exception;

}
