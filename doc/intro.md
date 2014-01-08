# Introduction to storm-clojure

<!--TODO: write [great documentation](http://jacobian.org/writing/great-documentation/what-to-write/)-->

## machine config

* zookeeer cluster: storm1 storme2 and storm3

* storm cluster: storm1 storm2 storm3 and storm4

## Nginx log format

```
log_format combined '$remote_addr - $remote_user 
[$time_iso8601] $msec $status $request_time
$upstream_response_time $body_bytes_sent
$http_x_app_id $http_x_signature $http_x_app_channel
$http_host "$request" "$http_referer"
"$http_user_agent"'
```
