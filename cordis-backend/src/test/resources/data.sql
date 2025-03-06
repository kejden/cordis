INSERT INTO role_entity (id, name)
SELECT 1, 'OWNER'
    WHERE NOT EXISTS (
    SELECT 1 FROM role_entity WHERE name = 'OWNER'
);

INSERT INTO role_entity (id, name)
SELECT 2, 'MODERATOR'
    WHERE NOT EXISTS (
    SELECT 1 FROM role_entity WHERE name = 'MODERATOR'
);

INSERT INTO role_entity (id, name)
SELECT 3, 'USER'
    WHERE NOT EXISTS (
    SELECT 1 FROM role_entity WHERE name = 'USER'
);