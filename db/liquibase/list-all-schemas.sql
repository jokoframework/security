select schema_name
from information_schema.schemata
 -- and to exclude 'system' schemata:
where schema_name <> 'information_schema'
  and schema_name !~ E'^pg_';
