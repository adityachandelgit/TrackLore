CREATE TABLE campsite_tracking
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    campground_id VARCHAR(20) NOT NULL,
    campsite_id   VARCHAR(20) NOT NULL,
    tracked_date  DATE        NOT NULL,
    site          VARCHAR(50),
    loop_name        VARCHAR(100),
    reserve_type  VARCHAR(50),
    campsite_type VARCHAR(100),
    type_of_use   VARCHAR(50),
    status        VARCHAR(20),
    quantity      INT       DEFAULT 0,
    max_people    INT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (campground_id, campsite_id, tracked_date)
);