CREATE TABLE client
(
    ucc          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email        VARCHAR(50)  NOT NULL UNIQUE,
    phone_number VARCHAR(12)  NOT NULL UNIQUE,
    username     VARCHAR(100) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    revoked      BOOLEAN DEFAULT FALSE
);

CREATE TABLE admin
(
    id        SERIAL PRIMARY KEY,
    email     VARCHAR(100) UNIQUE NOT NULL,
    password  VARCHAR(255)        NOT NULL,
    authority VARCHAR(20),
    is_banned BOOLEAN DEFAULT FALSE,
    otp       VARCHAR(6),
    mfa_type  VARCHAR(50)
)