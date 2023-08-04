CREATE TABLE bill(
    issue_time INT NOT NULL,
    is_sync INT DEFAULT 0
);

INSERT INTO bill(issue_time) VALUES (1);
INSERT INTO bill(issue_time) VALUES (2);