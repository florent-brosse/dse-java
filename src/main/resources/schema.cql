CREATE KEYSPACE ks WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };

CREATE TABLE ks.user (
    id text PRIMARY KEY,
    firstname text,
    lastname text,
    login text
);
