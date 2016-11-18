# aliyun-ddns-java
阿里云DDNS解析

准备条件：阿里云必须先买个域名（目前.win最便宜）

配置参数：（ddns.properties）
regionId=cn-hangzhou                      默认固定
accessKeyId=                              阿里云上查看
accessKeySecret=                          阿里云上查看
DomainName=                               阿里云上域名
RRs=@,www                                 需解析主机名
IpUrl=http://1212.ip138.com/ic.asp        可工查询本机公网IP的服务地址
Time=60                                   查询间隔（单位分钟）
