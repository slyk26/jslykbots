create table bot_settings
(
    id       serial primary key,
    guild_id varchar not null,
    setting  varchar not null,
    val      varchar
);

alter table bot_settings
    add constraint def unique (guild_id, setting);