create table reactroles (
       id bigint not null,
       channel_id bigint not null,
       emote varchar(100) not null,
       guild_id bigint not null,
       message_id bigint not null,
       role_id bigint not null,
       primary key (id)
);