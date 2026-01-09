CREATE TABLE books (
    id UUID PRIMARY KEY,
    isbn VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    version BIGINT,
    created_at BIGINT NOT NULL,
    updated_at BIGINT
);

CREATE TABLE borrowers (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT
);

