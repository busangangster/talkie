create table user
(
    user_seq           int auto_increment
        primary key,
    age                int           null,
    favorite           varchar(3000) null,
    gender             varchar(255)  null,
    is_not_first_login bit           not null,
    name               varchar(255)  null,
    user_id            varchar(255)  not null,
    remark             varchar(3000) null,
    birth              date          null
);

create table conversation_analytics
(
    conversation_seq   int auto_increment
        primary key,
    created_at         datetime(6)   not null,
    emotion_summary    varchar(3000) not null,
    title              varchar(255)  not null,
    vocabulary_summary varchar(3000) not null,
    word_cloud_summary varchar(3000) not null,
    user_seq           int           not null,
    constraint FKbi9r3r1qvlhlhcul9t1cdssp1
        foreign key (user_seq) references user (user_seq)
);

create table conversation_content
(
    conversation_content_seq int auto_increment
        primary key,
    content                  varchar(3000) not null,
    created_at               datetime(6)   not null,
    is_answer                bit           not null,
    user_seq                 int           null,
    conversation_seq         int           not null,
    constraint FK8k01qqdyutqsogox5fmup61uq
        foreign key (conversation_seq) references conversation_analytics (conversation_seq),
    constraint FK97hfdhwjtll1xkpr0uymar5dv
        foreign key (user_seq) references user (user_seq)
);

create table conversation_summary
(
    conversation_summary_seq int auto_increment
        primary key,
    content                  varchar(3000) null,
    conversation_seq         int           not null,
    constraint UKgwykee4eyc8cu5034ksgb2jug
        unique (conversation_seq),
    constraint FKjki8dfgtx6pdwu135gbrget88
        foreign key (conversation_seq) references conversation_analytics (conversation_seq)
);

create table day_analytics
(
    day_seq            int auto_increment
        primary key,
    amazing_score      int    not null,
    angry_score        int    not null,
    conversation_count int    not null,
    created_at         date   not null,
    happy_score        int    not null,
    love_score         int    not null,
    sad_score          int    not null,
    scary_score        int    not null,
    vocabulary_score   double not null,
    user_seq           int    not null,
    constraint FKn8sgv1disspgqqsjojmenmkyg
        foreign key (user_seq) references user (user_seq)
);

create table day_word_cloud
(
    day_word_cloud_seq int auto_increment
        primary key,
    count              int          not null,
    created_at         date         null,
    word               varchar(255) not null,
    day_seq            int          not null,
    constraint FKm9kxhqg0aj0axsiepaescv9d6
        foreign key (day_seq) references day_analytics (day_seq)
);

create table question
(
    question_seq int auto_increment
        primary key,
    content      varchar(3000) not null,
    created_at   datetime(6)   not null,
    is_active    bit           not null,
    user_seq     int           not null,
    constraint FKbhuqpcgdgq2wifb2j9k1e60t6
        foreign key (user_seq) references user (user_seq)
);

create table answer
(
    answer_seq   int auto_increment
        primary key,
    content      varchar(3000) not null,
    created_at   datetime(6)   not null,
    question_seq int           not null,
    constraint UKc4xddf4m8qqg2u2qm5sxjhcu2
        unique (question_seq),
    constraint FK1k9vsittgtmm3qixxtegr1wsu
        foreign key (question_seq) references question (question_seq)
);

create table sentiment
(
    sentiment_seq    int auto_increment
        primary key,
    amazing_score    int not null,
    angry_score      int not null,
    happy_score      int not null,
    love_score       int not null,
    sad_score        int not null,
    scary_score      int not null,
    conversation_seq int not null,
    constraint UKmg6socr6bolh393g5la92oaki
        unique (conversation_seq),
    constraint FK6b1bq8t31dpg9quv0yk82yr0i
        foreign key (conversation_seq) references conversation_analytics (conversation_seq)
);

create table vocabulary
(
    vocabulary_seq   int auto_increment
        primary key,
    vocabulary_score double null,
    conversation_seq int    not null,
    constraint UK1monq7a5m8othrrykoy4lysv2
        unique (conversation_seq),
    constraint FKpihwnmjug3hswurxfousku6dx
        foreign key (conversation_seq) references conversation_analytics (conversation_seq)
);

create table week_analytics
(
    week_seq           int auto_increment
        primary key,
    emotion_summary    varchar(3000) not null,
    month              int           not null,
    vocabulary_summary varchar(3000) not null,
    week               int           not null,
    word_cloud_summary varchar(3000) not null,
    year               int           not null,
    user_seq           int           not null,
    count_summary      varchar(3000) not null,
    constraint FKfqtly5nleis1s4gaudrb22lcr
        foreign key (user_seq) references user (user_seq)
);

create table week_word_cloud
(
    week_word_cloud_seq int auto_increment
        primary key,
    count               int          not null,
    word                varchar(255) not null,
    week_seq            int          not null,
    constraint FK3b9gissl529rot11igitgch81
        foreign key (week_seq) references week_analytics (week_seq)
);

create table word_cloud
(
    word_cloud_seq   int auto_increment
        primary key,
    count            int          not null,
    word             varchar(255) not null,
    conversation_seq int          not null,
    constraint FKk7u751sbmyhlvtga9yofeles4
        foreign key (conversation_seq) references conversation_analytics (conversation_seq)
);