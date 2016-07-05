#!/bin/sh

#
# $Id: mqbroker 587 2012-11-20 03:26:56Z shijia.wxr $
#

if [ -z "$DTUBE_HOME" ] ; then
  ## resolve links - $0 may be a link to maven's home
  PRG="$0"

  # need this for relative symlinks
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG="`dirname "$PRG"`/$link"
    fi
  done

  saveddir=`pwd`

  DTUBE_HOME=`dirname "$PRG"`/..

  # make it fully qualified
  DTUBE_HOME=`cd "$DTUBE_HOME" && pwd`

  cd "$saveddir"
fi

export DTUBE_HOME

nohup sh ${DTUBE_HOME}/bin/runserver.sh com.ict.dtube.filtersrv.FiltersrvStartup $@ &
