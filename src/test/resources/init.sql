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
values ('01', 'maximillian@nowhere.org', true, 'Maximillian', 'van der Schelde', 'as%gfg', true),
       ('02', 'maximus@company.com', true, 'Brutus', 'Maximus', 'as%gfg', true),
       ('03', 'sabrina-km@mail.org', true, 'Sabrina', 'Kühlemann', 'as%gfg', false),
       ('04', 'hein34@gmail.com', false, 'Heinz', 'Coleman', 'as%gfg', true),
       ('05', 'hanna@int-max.de', true, 'Hanna', 'Heitz-Steilberger', 'as%gfg', true);
