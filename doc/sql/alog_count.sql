CREATE TABLE `alog_count` (
    `id` int NOT NULL AUTO_INCREMENT,
    `url_id` int NOT NULL COMMENT 'url对应id',
    `time` datetime NOT NULL COMMENT '2013-08-27 17:32:00',
    `count` int NOT NULL COMMENT '当前分钟内的个数'
);

CREATE TABLE `alog_url` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(200) NOT NULL COMMENT '资源含义名字',
    `url` varchar(50) NOT NULL COMMENT '请求地址,非80端口需将端口号追加在url后面:',
    `uri` varchar(50) NOT NULL COMMENT '资源地址'
);
