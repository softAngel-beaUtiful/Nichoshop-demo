#!/bin/bash
set -eu

COMPONENT_DOMAIN=nichoshop
COMPONENT_NAME=nichoshop-backend

JAVA_HOME=/usr/lib/jvm/java
PATH="$JAVA_HOME/bin:$PATH"
LANG=en_US.UTF8

PID_FILE=/var/run/$COMPONENT_DOMAIN/$COMPONENT_NAME.pid

LOG_PATH=/var/log/$COMPONENT_DOMAIN
LOG_FILE=$LOG_PATH/$COMPONENT_NAME.log
STDOUT_LOG_FILE=$LOG_PATH/$COMPONENT_NAME.stdout.log
GC_LOG=$LOG_PATH/$COMPONENT_NAME-gc.log
HPROF_DIR=$LOG_PATH/hprof/$COMPONENT_NAME

LIB_PATH=/usr/lib/$COMPONENT_DOMAIN/$COMPONENT_NAME/lib

exec -a "$COMPONENT_NAME" \
java -Dfile.encoding=UTF8 \
     -Dhost.name=`/bin/hostname --fqdn` \
     -Dhttp.keepAlive=false \
     -Dcomponent.name=$COMPONENT_NAME \
     -Dlogging.path=$LOG_PATH \
     -Dservant.log.file=$STDOUT_LOG_FILE \
     -jar $LIB_PATH/nichoshop_backend-assembly-1.0.jar

echo $! > $PID_FILE