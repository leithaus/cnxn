#! /bin/sh
sudo /Users/lgm/work/src/devtools/mongo/mongodb-osx-x86_64-2.4.4/bin/mongo records --eval "db.dropDatabase()"; \
sudo /Users/lgm/work/src/devtools/rabbit/rabbitmq_server-2.6.1/sbin/rabbitmqctl stop_app; \
sudo /Users/lgm/work/src/devtools/rabbit/rabbitmq_server-2.6.1/sbin/rabbitmqctl reset; \
sudo /Users/lgm/work/src/devtools/rabbit/rabbitmq_server-2.6.1/sbin/rabbitmqctl stop; \
sudo sleep 2; \
mv KVDBLogs.log* history; \
sudo /Users/lgm/work/src/devtools/rabbit/rabbitmq_server-2.6.1/sbin/rabbitmq-server &


