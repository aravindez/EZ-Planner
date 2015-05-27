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
    ),
    (
        'mysterypanda',
        MD5('liu16y'),
        'liu16y@ncssm.edu',
        'Yuxuan',
        'Liu'
    ),
    (
        'morrison',
        MD5('mathsadist'),
        'morrison@ncssm.edu',
        'John',
        'Morrison'
    );
