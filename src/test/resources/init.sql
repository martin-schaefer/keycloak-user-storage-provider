create table customer
(
    id            varchar(100) not null primary key,
    email         varchar(100) not null,
    emailVerified boolean      not null,
    firstName     varchar(100) not null,
    lastName      varchar(100) not null,
    password      varchar(100) not null,
    enabled       boolean      not null
);
insert into customer (id, email, emailVerified, firstName, lastName, password, enabled)
values ('01', 'max1@nowhere.org', true, 'Max1', 'Müller1', 'as%gfg', true),
       ('02', 'max2@nowhere.org', true, 'Max2', 'Müller2', 'as%gfg', true),
       ('03', 'max3@nowhere.org', true, 'Max3', 'Müller3', 'as%gfg', true);
