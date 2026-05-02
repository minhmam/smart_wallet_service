create sequence if not exists notification_schedule_seq
    start with 1
    increment by 1;

create table if not exists notification_schedules (
    id bigint primary key default nextval('notification_schedule_seq'),
    title varchar(255) not null,
    content text not null,
    target_group varchar(50) not null,
    schedule_state varchar(50) not null,
    recurrence_type varchar(50) not null,
    start_at timestamp not null,
    next_run_at timestamp,
    last_run_at timestamp,
    last_execution_status varchar(50),
    last_target_user_count bigint default 0,
    last_success_count bigint default 0,
    last_failure_count bigint default 0,
    failure_reason varchar(500),
    retry_count integer default 0,
    created_at timestamp,
    updated_at timestamp,
    created_by varchar(255),
    updated_by varchar(255),
    status integer default 1
);

create index if not exists idx_notification_schedules_status_state_next_run
    on notification_schedules (status, schedule_state, next_run_at);

create index if not exists idx_notification_schedules_target_group
    on notification_schedules (target_group);

create sequence if not exists user_notification_token_seq
    start with 1
    increment by 1;

create table if not exists user_notification_tokens (
    id bigint primary key default nextval('user_notification_token_seq'),
    user_id bigint not null,
    fcm_token text not null,
    platform varchar(50),
    device_id varchar(255),
    active boolean default true,
    last_used_at timestamp,
    created_at timestamp,
    updated_at timestamp,
    created_by varchar(255),
    updated_by varchar(255),
    status integer default 1,
    constraint fk_user_notification_tokens_user
        foreign key (user_id) references users (id)
);

create unique index if not exists uk_user_notification_tokens_fcm_token
    on user_notification_tokens (fcm_token);

create index if not exists idx_user_notification_tokens_user_active
    on user_notification_tokens (user_id, active, status);
