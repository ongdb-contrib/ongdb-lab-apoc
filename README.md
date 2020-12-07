# ONgDB OLAB PLUGIN
```
CALL dbms.functions() YIELD name,signature,description,roles WHERE name CONTAINS 'olab' RETURN name,signature,description,roles
```
```
CALL dbms.procedures() YIELD name,signature,description,roles,mode WHERE name CONTAINS 'olab' RETURN name,signature,description,roles,mode
```

