Weike Member Scale in ES
========================

# 统计
## m-s分布
- 未过期的卖家+会员列 totalSeller-10806 totalMember-1102513181

`[0,50w)-10371,[50,100)-298,[100,200)-115,[200,300)-19,[300,400)-1,[400,500)-0,[500,)-2`
- 所有卖家+会员列表.txt totalSeller-99554 totalMember-3262225375

`[0,50w)-98569,[50,100)-695,[100,200)-234,[200,300)-42,[300,400)-7,[400,500)-2,[500,)-5`
- 预估 totalSeller-30w totalMember-100e

`[0,50w)-30w,[50,100)-1500,[100,200)-600,[200,300)-100,[300,400)-20,[400,500)-8,[500,)-15`

## 会员数据量
- 每个会员存储量
<pre>
9,919,781 docs 4.55GB          ->  0.481K/doc
10,109,781 docs 4.61GB         ->  0.478K/doc
117,023,658 docs 50.88GB       ->  0.456K/doc
118,575,396 docs 51.49GB       ->  0.455K/doc
</pre>
- 100w~500M 1e~50G

## 卖家查询性能
- 配置
  - jmeter机器4和16g  CentOS Linux release 7.0.1406 Intel(R) Xeon(R) CPU E5-2650 v2 @ 2.60GHz
  - ES机器8核8g
  - ES 1-node1-shard0-replica  v2.4.1
  - JVM -Xms6g -Xmx6g -Xmn5120M -XX:CMSInitiatingOccupancyFraction=90
  - 总数据量 =1e ~50GB
  - 测试样本1w=20线程执行500次
- count
序号|卖家| 操作| 样本| qps| 平均时|错误率| 同时操作 
---|---|---|---|---|---|---|---
1|sellerid=291177172 doc=296035 | C|1w| 30.0| 652| 0% | CS 60w/20m(测试期间导入数据量)
2|sellerid=897258160 doc=487000|C|1w|31.3|622|0%|CS
3|sellerid=1110040095 doc=1024000|C|1w|22.5|870 |0%|CS 
4|sellerid=20868741 doc=2558502|C|1w|9.0|2191|0.01%|CS
5|sellerid=479184430 doc=5556000|C|1w|8.8|2249|0.01%|CS
6|sellerid=842343977 doc=7373174|C|1w|5.0|4005|10.77%|CS

- search
序号|卖家| 操作| 样本| qps| 平均时|错误率| 同时操作 
---|---|---|---|---|---|---|---
1|sellerid=291177172 doc=296035 | C|1w|12.4| 1573| 0.08% | CS 60w/20m(测试期间导入数据量)
2|sellerid=897258160 doc=487000|C|1w|12.4|1566|0.06%|CS
3|sellerid=1110040095 doc=1024000|C|1w|12.5|1563 |0.11%|CS
4|sellerid=20868741 doc=2558502|C|1w|8.8|2244|0%|CS
5|sellerid=479184430 doc=5556000|C|1w|8.5|2324|0%|CS
6|sellerid=842343977 doc=7373174|C|1w|4.9|4026|11.05%|CS

- 总结
  - C和S的性能主要是在查询总量(单个seller的会员数量)，其次与总数据量有关系(TODO-在1e、5e、10e性能影响)(TODO-单机控制多大量)
  - C和S随着查询总量越多越慢，500w以上C和S的性能就不太好，在100w以内性能不错，200w－500w之间可以接收。若机器性能好些估计C和S的性能会更好
  - seller会员100w以内的放在一个shard里，以上的考虑添加shard，差不多保证单shard100w(配置好的话量级可以多)

# 目标
- 数据量100e*2(1RS) 1kw/seller 200e/10e(500g TODO) = 20台机器
- 性能要求
    - qps 200集群
    - rspTime
        - count 1s  20并发单机
        - search 2s 20并发单机
        - index 1s

# 原则
- 1机器1node 
- 1个索引类型在一个shard里数据量控制在500w(8核8g机器)
- jvm新生代尽量大些
- linux禁用swap内存交换

# 方案
## 常量定义
## 索引方案
### 系统配置
- 8核 16G 500G * 16台
- ES jvm分配8G [1Node nIndex]/1台  集群-8P * (1+1R) = 16台 
- 30e/16~2e/1台～100G/台 200e/16~12e/1台~600G/1台(TODO-验证)
### mysql表总结
- es_idx 系统索引表 定义集群里索引和使用情况 - es插件管理索引(create|del|view，close|open，reindex，statistics|analysis)
列名|类型|描述
---|---|---
id          | int       | 索引id
idx         | varchar   | 索引名
shard       | int       | 主分片数
replica     | int       | 副本数
cluster     | varchar   | 集群名称 weike
min_size    | int       | 会员开始数
max_size    | int       | 会员结束数
usage       | int       | 使用的卖家数 -> 匹配规则处理
limit_usage | int       | usage的最大值
is_route    | int       | 0-不需要route，1-启用route
route       | varchar   | route规则
is_open     | int       | 索引状态 close-0 open-1
comment     | varchar   | 备注
create_time | datetime  | 
update_time | datetime  |
is_delete   | int       | 1-delete
- es_reidx seller索引升级表 描述索引升级过程
列名|类型|描述
---|---|---
id          | bigint    | id
name        | varchar   | sellerid
mark        | int       | 升级标示
from        | int       | 原索引
to          | int       | 目标索引
status      | int       | 0-suc 1-fail 2-run
create_time | datetime  | 
update_time | datetime  |
is_delete   | int       | 1-delete

### 会员索引分布 （TODO-过期用户处理）
会员数|索引配置|索引分配|路由
---|---|---|---
[0-100]     |   8P1R    | 放在一个系统索引里                   | route TODO-保证route平衡的算法 -> 直接的sellerid做路由不一定合适
[100-500]   |   4P1R    | 平均放在几个系统索引，如16台->2个索引   | TODO-是否需要
[500-1000]  |   8P1R    | 单独系统索引                        | 
### seller索引alias
- 每个seller，默认创建一个读索引化名(rsellerid)，一个写索引化名(wsellerid)
- 索引分配依据seller会员数+系统索引承载量(系统索引包含的seller数)               
### reindex - seller索引升级 会员赠长
- index1 -> index2
    - 会员数量定期检查，发现可升级索引。会员数量发现-监控慢接口
    - 启动升级 (无需停机，不丢数据迁移)
        - 定义开始迁移标示1,修改写索引化名到index2(保证新数据到新索引里),修改读索引化名到index1和index2
        - 定义开始迁移标示2,修改读索引化名到index2,scrollSearch和bulkIndex批量迁移数据 (可定制时间)。标示2可告诉用户正在优化 进度等
        - 完成迁移，更新索引升级表和系统索引表
- 定时检查程序       
    - 是否reindex
    - 数据量和索引方案，区间数据量可左右100w
## 多个集群
### tribe 
https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-tribe.html
<pre>
tribe:
    t1: 
        cluster.name:   cluster_one
    t2: 
        cluster.name:   cluster_two

tribe:
    blocks:
        write:    true
        metadata: true
        
tribe:
    blocks:
        write.indices:    hk*,ldn*
        metadata.indices: hk*,ldn*


</pre>    
  
    
# cluster部署
## master node
<pre>
node.master: true 
node.data: false
discovery.zen.minimum_master_nodes: 2

path.data:  /var/elasticsearch/data
node.max_local_storage_nodes: 1

PUT _cluster/settings
{
  "transient": {
    "discovery.zen.minimum_master_nodes": 2
  }
}
</pre>
## data node
<pre>
node.master: false
node.data: true
path.data:  /var/elasticsearch/data
node.max_local_storage_nodes: 1
</pre>
## client node
<pre>
node.master: false
node.data: false
</pre>

# es client
## 配置
es.http.host= 192.168.1.1
es.http.port= 9002
## feature
- alias存在判断？
- 性能统计

# 未过期会员迁移
## 数据量 5e
## 机器配置
<pre>
1台nginx  或者aliyun内网的lbs
2台cient  4核4g
3台master 4核4g
16台node  4核8g
</pre>
## 脚本 -> web


# 监控
## 系统监控
磁盘监控
## 接口监控
慢接口监控

# 问题：
- 1node1shard 最大多少E
- seller的会员数如何得知，在创建索引的时候(在卖家购买时创建)
- 过期卖家，不希望占用系统资源


对应关系保存在redis/mysql里的系统索引 (TODO-多读多写)

索引shard分配
doc route
alias设计

zk

冗余

sellerid和

过期卖家处理

数据存储方案ES-hadoop


