create table serverchannels (
       id bigint not null,
       guild_id bigint not null,
       channel_id bigint not null,
       specifier varchar(255) not null,
       primary key (id)
);