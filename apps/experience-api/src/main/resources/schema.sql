create table if not exists disruption_case (
    case_id varchar(64) primary key,
    booking_reference varchar(32) not null,
    disruption_type varchar(32) not null,
    detected_at timestamptz not null,
    impacted_passengers integer not null,
    delay_minutes integer not null,
    connection_at_risk boolean not null,
    overnight_impact boolean not null,
    high_value_itinerary boolean not null,
    customer_id varchar(64) not null,
    customer_full_name varchar(128) not null,
    loyalty_tier varchar(32) not null,
    traveling_with_children boolean not null,
    requires_accessibility_support boolean not null,
    vip_customer boolean not null,
    corporate_traveler boolean not null,
    recommendation_action varchar(32) not null,
    recommendation_score integer not null,
    recommendation_summary text not null,
    recommendation_explanation text not null,
    recommendation_priority varchar(32) not null,
    recommendation_sla_bucket varchar(32) not null,
    recommendation_human_review_required boolean not null,
    recommendation_premium_customer boolean not null,
    recommendation_reasons text not null,
    updated_at timestamptz not null
);

create table if not exists disruption_case_segment (
    case_id varchar(64) not null,
    segment_order integer not null,
    origin varchar(8) not null,
    destination varchar(8) not null,
    marketing_carrier varchar(8) not null,
    flight_number varchar(16) not null,
    scheduled_departure_at timestamptz not null,
    scheduled_arrival_at timestamptz not null,
    primary key (case_id, segment_order),
    constraint fk_segment_case foreign key (case_id) references disruption_case(case_id) on delete cascade
);

alter table disruption_case add column if not exists high_value_itinerary boolean not null default false;
alter table disruption_case add column if not exists vip_customer boolean not null default false;
alter table disruption_case add column if not exists corporate_traveler boolean not null default false;
alter table disruption_case add column if not exists recommendation_priority varchar(32) not null default 'STANDARD';
alter table disruption_case add column if not exists recommendation_sla_bucket varchar(32) not null default '4_HOURS';
alter table disruption_case add column if not exists recommendation_human_review_required boolean not null default false;
alter table disruption_case add column if not exists recommendation_premium_customer boolean not null default false;
