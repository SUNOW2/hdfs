## 简介
本项目是hadoop集群的应用服务器端实现

### 项目结构
springBoot + mybatis + mysql + HdFs API

### 实现
功能：上传文件，下载文件，删除文件，查询文件等。<br />
数据库：hdfs_no， old_name， new_name， is_del， hdfs_date。<br />
controller层接收文件：FineUploader。<br />

### 搭建hadoop集群
docker-compose up -d<br />
更改hosts文件内容<br />