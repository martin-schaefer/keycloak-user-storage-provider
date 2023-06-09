/* activation code schema */
create table activation_code
(
    code         varchar(12)   not null primary key,
    codeGroup    varchar(100)  not null,
    recipient    varchar(100)  not null,
    grantedRole  varchar(1000) not null,
    created      timestamp     not null,
    validUntil   date          not null,
    redemption   timestamp,
    redemptionBy varchar(100)
);
create unique index code_ci on activation_code (upper(code));

/* customer schema */
create table customer
(
    customerId    varchar(100) not null primary key,
    email         varchar(100) not null,
    emailVerified boolean      not null,
    firstName     varchar(100),
    lastName      varchar(100),
    password      varchar(100),
    enabled       boolean      not null
);
create unique index email_ci on customer (lower(email));
/* test data */
insert into customer (customerId, email, emailVerified, firstName, lastName, password, enabled)
values ('01', 'maximillian@nowhere.org', true, 'Maximillian', 'van der Schelde', 'as%gfg', true),
       ('02', 'maximus@company.com', true, 'Brutus', 'Maximus', 'as%gfg', true),
       ('03', 'sabrina-km@mail.org', true, 'Sabrina', 'Kühlemann', 'as%gfg', false),
       ('04', 'hein34@gmail.com', false, 'Heinz', 'Coleman', 'as%gfg', true),
       ('05', 'hanna@int-max.de', true, 'Hanna', 'Heitz-Steilberger', 'as%gfg', true);
