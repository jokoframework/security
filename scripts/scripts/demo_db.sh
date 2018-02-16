# DEMO DB SCRIPT
echo -e "33[33m DEMO DB SCRIPT :D"

export ENV_VARS="/opt/finhealth/development.vars"

./scripts/updater fresh

./scripts/updater seed src/main/resources/db/sql/seed-data.sql

./scripts/updater seed src/main/resources/db/sql/seed-demo.sql

./scripts/updater seed src/main/resources/db/sql/seed-demo-sprint-11.sql
./scripts/updater seed src/main/resources/db/sql/seed-demo-sprint-12.sql


