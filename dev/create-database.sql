SELECT 'CREATE DATABASE template' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'template')\gexec
GRANT ALL PRIVILEGES ON DATABASE template TO postgres;