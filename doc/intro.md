# Introduction to storm-clojure

TODO: write [great documentation](http://jacobian.org/writing/great-documentation/what-to-write/)

## Nginx log format

```
log_format combined '$remote_addr - $remote_user 
[$time_iso8601] $msec $status $request_time
$upstream_response_time $body_bytes_sent
$http_x_app_id $http_x_signature $http_x_app_channel
"$request" "$http_referer"
"$http_user_agent"'
```
