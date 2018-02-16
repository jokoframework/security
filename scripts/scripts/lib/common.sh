#!/bin/bash

function check_status() {
    status=$1
    if [ ${status} -ne 0 ]; then
        echo "Program exited with code: $status"
	_EXIT_STATUS=${status}
	exit_handler
    fi
}

function exit_handler(){
    exit $_EXIT_STATUS
}

[ -z "${ENV_VARS}" ] && echo "ERROR: No environment variable ENV_VARS defined. Aborting" && exit 1
[ ! -f "${ENV_VARS}" ] && echo "ERROR: The ENV_VARS does not exist or is not pointing to a file. Aborting." && exit 1
type mvn >/dev/null 2>&1 || { echo >&2 "ERROR: I require 'mvn' but it's not installed. Aborting."; exit 1; }

source ${ENV_VARS}

[ -z "$PROFILE_DIR" ] && echo "ERROR: PROFILE_DIR not defined in ${ENV_VARS}. Aborting" && exit 1

#[ $# -eq 0 ] && echo "ERROR: No parameter provided" && exit 2

sys=$(uname);
if [ $sys != "MINGW32_NT-6.2" ]; then
    MY_IP="`/sbin/ifconfig | awk '$1 == "inet" { print $2 }'|head -1 | awk -F\: '{print $2}'`";
else
    MY_IP="unknown";
fi
# si estamos dentro de sodep
for i in `scripts/lib/get-ip.sh`; do 
	if [[ "$i" =~ ^10\.1\.* || "$MY_IP" =~ ^10\.0\.* ]]; then 
		SODEP="si"; 
	else 
		SODEP=""; 
	fi 
done
PROP_FILE=${PROFILE_DIR}/application.properties

URL=$(grep ^spring.datasource.url $PROP_FILE | cut -d '=' -f 2)
HOSTPORT=$(echo $URL |  cut -d '/' -f 3)
PORT=$(echo $HOSTPORT | cut -d ':' -f 2)
HOST=$(echo $HOSTPORT | cut -d '\' -f 1)
DB=$(echo $URL | cut -d '/' -f 4)

USERNAME=$(grep ^spring.datasource.username $PROP_FILE | cut -d '=' -f 2)
PASSWORD=$(grep ^spring.datasource.password $PROP_FILE | cut -d '=' -f 2)

export PGPASSWORD=$PASSWORD
export MY_IP

