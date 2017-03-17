# set cronjob for dumping

## shell script

following http://www.cyberciti.biz/faq/run-execute-sh-shell-script/

### move script to folder
```
mv /tmp/dump-dh.sh /opt/
```

### set execute permission on script
```
chmod +x /opt/dump-dh.sh
```

### run script for test
```
sh /opt/dump-dh.sh
```

## cronjob

following https://www.stetic.com/developer/cronjob-linux-tutorial-und-crontab-syntax.html

### edit crontab

```
nano /etc/crontab
```

### set cronjob

#### every minute
```
* * * * * root /opt/dump-dh.sh
```
#### two times a day (11:59h and 23:59h)
```
59 11 * * * root /opt/dump-dh.sh
59 23 * * * root /opt/dump-dh.sh
```

### start service if not started
```
service crond start
```
