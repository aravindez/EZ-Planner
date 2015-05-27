-- ---
-- TASK TEST DATA
-- insert some tasks for each user
-- ---

INSERT INTO task
    (
        name,
        created_on,
        due,
        content
    )
    VALUES
    (
        'numerical assignment',
        CURDATE(),
        '2015-05-27',
        'the differential equations program'
    ),
    (
        'numerical comp time',
        CURDATE(),
        '2015-05-22',
        'COMP TIME!'
    ),
    (
        'miller essay',
        CURDATE(),
        '2015-05-11',
        'essay due forever ago'
    ),
    (
        'practical',
        CURDATE(),
        '2015-05-19',
        'adv prog lab practical'
    );
