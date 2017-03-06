# jingcai-commons

实用工具大杂烩

包含如下内容:

1. common-context  
    与特定工具相关的辅助类, 如:`com.jingcai.apps.common.context.conf.CommonConfigureProcessor`通过使用aspectj表达式,过滤spring bean, 对这些bean, 修改`scope`, `lazy`, `property ref`和`property value`等
2. common-ice  
    用于支持zeroc-ice框架 
3. common-lang  
    com.jingcai.apps.common.lang.concurrent.DistributeLock, 利用curator实现分布式锁  
    com.jingcai.apps.common.lang.diskq.DiskQueuePool, 一个高性能的支持持久化到本地文件系统的队列
    com.jingcai.apps.common.lang.encrypt.*, 包含各种方式的加密  
    com.jingcai.apps.common.lang.id.IdGenerator, 利用zk, 生成分布式环境下的id  
    com.jingcai.apps.common.lang.serialize.* 支持序列化, 如kyro  
    com.jingcai.apps.common.lang.reflect.* 支持反射, 如dozer相关操作  
4. common-jdbc  
    支持mybatis, redis相关的操作
5. common-test
    支持dao, service, business层的单元测试
    
