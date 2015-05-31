-- ---
-- USER TEST DATA
-- inserts both of our user info as well as Morrison's
-- ---

INSERT INTO `user`
    (
        username,
        password,
        email,
        first_name,
        last_name
    )
    VALUES
    (
        'aravindez',
        MD5('avihome97'),
        'aravindez@gmail.com',
        'Aravind',
        'Ez'
    );
