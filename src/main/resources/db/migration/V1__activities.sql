CREATE TABLE activities (id BIGINT AUTO_INCREMENT,
								start_time TIMESTAMP,
								activity_desc VARCHAR(255),
								activity_type VARCHAR(255),
								PRIMARY KEY (id));

CREATE TABLE track_points (id BIGINT AUTO_INCREMENT,
                            act_time DATE,
                            lat DOUBLE,
                            lon DOUBLE,
                            activityId BIGINT,
                            PRIMARY KEY (id),
                            FOREIGN KEY (activityId) REFERENCES activities(id));

INSERT INTO activities(start_time, activity_desc, activity_type) VALUES ('2021-3-15 10:23','Basketball in park','BASKETBALL');
INSERT INTO activities(start_time, activity_desc, activity_type) VALUES ('2020-3-15 10:23','Biking at Buda','BIKING');
INSERT INTO activities(start_time, activity_desc, activity_type) VALUES ('2020-3-14 10:23','Hiking in zoo','HIKING');
