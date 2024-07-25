alter table if exists "tweet" drop constraint if exists FKfy08hmkhog3me1vkqadvlo18l;
alter table if exists "tweet" drop constraint if exists FKotiyogfsv9d0vdvpmpo9v798j;
alter table if exists "tweet" drop constraint if exists FKkum0tahoa47yhd7gyjrvx62mw;
alter table if exists "tweet_hashtags" drop constraint if exists FKda2s9mo23agyi9idx8q00xcb3;
alter table if exists "tweet_hashtags" drop constraint if exists FK3sr141h5t7kc9vy9wy9xeirsc;
alter table if exists user_followers drop constraint if exists FK1ch1jm0u197wmysjti3doq4rf;
alter table if exists user_followers drop constraint if exists FKn0vc0ca4f6d0ubyjnvaw6dsnk;
alter table if exists user_likes drop constraint if exists FK2g3x1faq503wiewg2u8nemtm;
alter table if exists user_likes drop constraint if exists FK3bctt4p4v058f5wtf862ue3a2;
alter table if exists user_mentions drop constraint if exists FK345tq76s5yef2ee4djt5wjwfa;
alter table if exists user_mentions drop constraint if exists FKkj6cw2qdcc5i4vvlx3ipo95qv;
drop table if exists "hashtag" cascade;
drop table if exists "tweet" cascade;
drop table if exists "tweet_hashtags" cascade;
drop table if exists user_followers cascade;
drop table if exists user_likes cascade;
drop table if exists user_mentions cascade;
drop table if exists "user_table" cascade;
CREATE TABLE "hashtag"
  (
     id         INT8 GENERATED BY DEFAULT AS IDENTITY,
     first_used TIMESTAMP,
     LABEL      VARCHAR(255) NOT NULL,
     last_used  TIMESTAMP,
     PRIMARY KEY (id)
  );

CREATE TABLE "tweet"
  (
     id            INT8 GENERATED BY DEFAULT AS IDENTITY,
     content       VARCHAR(255),
     deleted       BOOLEAN NOT NULL,
     posted        TIMESTAMP,
     author        INT8,
     "in_reply_to" INT8,
     "repost_of"   INT8,
     PRIMARY KEY (id)
  );

CREATE TABLE "tweet_hashtags"
  (
     tweet_id   INT8 NOT NULL,
     hashtag_id INT8 NOT NULL
  );

CREATE TABLE user_followers
  (
     follower_id  INT8 NOT NULL,
     following_id INT8 NOT NULL,
     PRIMARY KEY (follower_id, following_id)
  );

CREATE TABLE user_likes
  (
     user_id  INT8 NOT NULL,
     tweet_id INT8 NOT NULL,
     PRIMARY KEY (user_id, tweet_id)
  );

CREATE TABLE user_mentions
  (
     user_id  INT8 NOT NULL,
     tweet_id INT8 NOT NULL,
     PRIMARY KEY (user_id, tweet_id)
  );

CREATE TABLE "user_table"
  (
     id         INT8 GENERATED BY DEFAULT AS IDENTITY,
     PASSWORD   VARCHAR(255) NOT NULL,
     username   VARCHAR(255) NOT NULL,
     deleted    BOOLEAN NOT NULL,
     joined     TIMESTAMP,
     email      VARCHAR(255) NOT NULL,
     first_name VARCHAR(255),
     last_name  VARCHAR(255),
     phone      VARCHAR(255),
     PRIMARY KEY (id)
  );
alter table if exists "tweet" add constraint
    FKfy08hmkhog3me1vkqadvlo18l
        foreign key (author) references "user_table";

alter table if exists "tweet" add constraint
    FKotiyogfsv9d0vdvpmpo9v798j
        foreign key ("in_reply_to") references "tweet";

alter table if exists "tweet" add constraint
    FKkum0tahoa47yhd7gyjrvx62mw
        foreign key ("repost_of") references "tweet";

alter table if exists "tweet_hashtags" add constraint
    FKda2s9mo23agyi9idx8q00xcb3
        foreign key (hashtag_id) references "hashtag";

alter table if exists "tweet_hashtags" add constraint
    FK3sr141h5t7kc9vy9wy9xeirsc
        foreign key (tweet_id) references "tweet";

alter table if exists user_followers add constraint
    FK1ch1jm0u197wmysjti3doq4rf
        foreign key (following_id) references "user_table";

alter table if exists user_followers add constraint
    FKn0vc0ca4f6d0ubyjnvaw6dsnk
        foreign key (follower_id) references "user_table";

alter table if exists user_likes add constraint
    FK2g3x1faq503wiewg2u8nemtm
        foreign key (tweet_id) references "tweet";

alter table if exists user_likes add constraint
    FK3bctt4p4v058f5wtf862ue3a2
        foreign key (user_id) references "user_table";

alter table if exists user_mentions add constraint
    FK345tq76s5yef2ee4djt5wjwfa
        foreign key (tweet_id) references "tweet";

alter table if exists user_mentions add constraint
    FKkj6cw2qdcc5i4vvlx3ipo95qv
        foreign key (user_id) references "user_table";

