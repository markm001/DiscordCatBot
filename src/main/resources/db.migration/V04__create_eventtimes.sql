create table usereventtimes (
       id bigint not null,
       available_time datetime(6) not null,
       event_id bigint not null,
       user_id bigint not null,
       primary key (id)
);

create table usertimezones (
       user_id bigint not null,
       time_zone varchar(255) not null,
       primary key (user_id)
);