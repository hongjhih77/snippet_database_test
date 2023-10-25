CREATE TABLE inventory(
    item_id INT NOT NULL PRIMARY KEY,
    quantity INT DEFAULT 0
);

INSERT INTO inventory(item_id, quantity) VALUES (1, 10);