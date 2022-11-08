create table members (
       id bigint not null,
       bot_permissions bigint not null,
       guild_id bigint not null,
       member_id bigint not null,
       primary key (id)
);