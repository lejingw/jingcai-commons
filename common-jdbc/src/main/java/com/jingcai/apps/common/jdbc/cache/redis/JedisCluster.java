package com.jingcai.apps.common.jdbc.cache.redis;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;
import redis.clients.jedis.params.set.SetParams;

import java.util.Collections;
import java.util.Set;

public class JedisCluster extends BinaryJedisCluster /*implements JedisClusterCommands,
        MultiKeyJedisClusterCommands, JedisClusterScriptingCommands*/ {

  public JedisCluster(HostAndPort node) {
    this(Collections.singleton(node), DEFAULT_TIMEOUT);
  }

  public JedisCluster(HostAndPort node, int timeout) {
    this(Collections.singleton(node), timeout, DEFAULT_MAX_REDIRECTIONS);
  }

  public JedisCluster(HostAndPort node, int timeout, int maxRedirections) {
    this(Collections.singleton(node), timeout, maxRedirections, new GenericObjectPoolConfig());
  }

  public JedisCluster(HostAndPort node, final GenericObjectPoolConfig poolConfig) {
    this(Collections.singleton(node), DEFAULT_TIMEOUT, DEFAULT_MAX_REDIRECTIONS, poolConfig);
  }

  public JedisCluster(HostAndPort node, int timeout, final GenericObjectPoolConfig poolConfig) {
    this(Collections.singleton(node), timeout, DEFAULT_MAX_REDIRECTIONS, poolConfig);
  }

  public JedisCluster(HostAndPort node, int timeout, int maxRedirections,
                      final GenericObjectPoolConfig poolConfig) {
    this(Collections.singleton(node), timeout, maxRedirections, poolConfig);
  }

  public JedisCluster(HostAndPort node, int connectionTimeout, int soTimeout,
                      int maxRedirections, final GenericObjectPoolConfig poolConfig) {
    super(Collections.singleton(node), connectionTimeout, soTimeout, maxRedirections, poolConfig);
  }

  public JedisCluster(Set<HostAndPort> nodes) {
    this(nodes, DEFAULT_TIMEOUT);
  }

  public JedisCluster(Set<HostAndPort> nodes, int timeout) {
    this(nodes, timeout, DEFAULT_MAX_REDIRECTIONS);
  }

  public JedisCluster(Set<HostAndPort> nodes, int timeout, int maxRedirections) {
    this(nodes, timeout, maxRedirections, new GenericObjectPoolConfig());
  }

  public JedisCluster(Set<HostAndPort> nodes, final GenericObjectPoolConfig poolConfig) {
    this(nodes, DEFAULT_TIMEOUT, DEFAULT_MAX_REDIRECTIONS, poolConfig);
  }

  public JedisCluster(Set<HostAndPort> nodes, int timeout, final GenericObjectPoolConfig poolConfig) {
    this(nodes, timeout, DEFAULT_MAX_REDIRECTIONS, poolConfig);
  }

  public JedisCluster(Set<HostAndPort> jedisClusterNode, int timeout, int maxRedirections,
                      final GenericObjectPoolConfig poolConfig) {
    super(jedisClusterNode, timeout, maxRedirections, poolConfig);
  }

  public JedisCluster(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout,
                      int maxRedirections, final GenericObjectPoolConfig poolConfig) {
    super(jedisClusterNode, connectionTimeout, soTimeout, maxRedirections, poolConfig);
  }

  
  public void close() {
    if (connectionHandler != null) {
      for (JedisPool pool : connectionHandler.getNodes().values()) {
        try {
          if (pool != null) {
            pool.destroy();
          }
        } catch (Exception e) {
          // pass
        }
      }
    }
  }

  
  public String set(final String key, final String value) {
    return new JedisClusterCommand<String>(connectionHandler, maxRedirections) {
      public String execute(Jedis connection) {
        return connection.set(key, value);
      }
    }.run(key);
  }
  public String set(final String key, final String value, final SetParams setParams) {
    return new JedisClusterCommand<String>(connectionHandler, maxRedirections) {
      public String execute(Jedis connection) {
        return connection.set(key, value, setParams);
      }
    }.run(key);
  }

  public String set(final byte[] key, final byte[] value) {
    return new JedisClusterCommand<String>(connectionHandler, maxRedirections) {
      public String execute(Jedis connection) {
        return connection.set(key, value);
      }
    }.runBinary(key);
  }

  public String set(final String key, final byte[] value, final SetParams setParams) {
    final byte[] key2 = key.getBytes();
    return new JedisClusterCommand<String>(connectionHandler, maxRedirections) {
      public String execute(Jedis connection) {
        return connection.set(key2, value, setParams);
      }
    }.run(key);
  }

  
  public String get(final String key) {
    return new JedisClusterCommand<String>(connectionHandler, maxRedirections) {
      public String execute(Jedis connection) {
        return connection.get(key);
      }
    }.run(key);
  }

  public byte[] getBytes(final byte[] key) {
    return new JedisClusterCommand<byte[]>(connectionHandler, maxRedirections) {
      public byte[] execute(Jedis connection) {
        return connection.get(key);
      }
    }.runBinary(key);
  }

  
  public Boolean exists(final String key) {
    return new JedisClusterCommand<Boolean>(connectionHandler, maxRedirections) {
      public Boolean execute(Jedis connection) {
        return connection.exists(key);
      }
    }.run(key);
  }
  public Boolean exists(final byte[] key) {
    return new JedisClusterCommand<Boolean>(connectionHandler, maxRedirections) {
      public Boolean execute(Jedis connection) {
        return connection.exists(key);
      }
    }.runBinary(key);
  }
}
