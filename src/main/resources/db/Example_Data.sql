INSERT INTO `user` (`email`, `password`) VALUES
    ('test@test.com', 'secret');

INSERT INTO `ingredient` (`name`) VALUES
    ('Fettuccini pasta'), ('Butter'), ('Heavy cream'), ('Salt'), ('Pepper'), ('Garlic'), ('Parmesan cheese'),
    ('Romano cheese'), ('Water'), ('Sugar'), ('Soy sauce'), ('Vinegar'), ('Chicken thigh'), ('Chicken breast'),
    ('Chocolate chip'), ('Egg'), ('Coffee'), (':-)');

INSERT INTO `recipe` (`title`, `description`, `approximate_completion_time`, `public`, `author`) VALUES
    ('Fettuccine Alfredo', 'This is a recipe that I created by modifying my mother\'s recipe.', 130, 1, 1),
    ('Chocolate Chip Brownies', 'Best brownies I\'ve ever had!', 1232, 1, 1),
    ('Cappuccino Brownies', 'Great, creamy brownies. Freeze great. Wonderful to give as Christmas presents to teachers and friends.', 123, 1, 1),
    ('Baked Teriyaki Chicken', 'A much requested chicken recipe! Easy to double for a large group. Delicious!', 321, 1, 1),
    ('Grandma\'s Apple Pie', 'Tastes like grandma...', 0, 1, 1),
    ('Thai Beef', 'A delicious meal blending the flavors of salt and crushed dreams.', 0, 1, 1),
    ('Hash Brownies', 'They taste funny...', 10, 1, 1),
    ('Baked Chicken', 'Flavorful and moist baked chicken.', 11, 1, 1),
    ('Chicken Pot Pie', 'Easy, beginner pot pie that not even your mother could love.', 42, 1, 1),
    ('Stuffed Chicken Breast', 'This is a great dish for the fall.', 1337, 1, 1);

INSERT INTO `recipe_has_ingredient` (`recipe`, `ingredient`, `amount`, `unit`) VALUES
    (5, 10, 50, 16), (1, 1, 24, 6), (5, 2, 5, 12), (2, 2, 2, 11), (2, 9, 2, 11), (2, 15, 2, 12), (3, 2, 2, 11),
    (3, 15, 2, 12), (3, 9, 2, 11), (5, 16, 2, 5), (6, 4, 2, 10), (7, 15, 2, 12), (9, 3, 2, 12), (1, 7, 1, 12),
    (1, 6, 1, 10), (1, 8, 1, 12), (1, 2, 1, 12), (2, 10, 1, 12), (2, 18, 1, 12), (3, 10, 1, 12), (4, 13, 1, 20),
    (4, 12, 1, 11), (4, 11, 1, 12), (4, 5, 1, 3), (6, 11, 1, 12), (7, 17, 1, 24), (8, 14, 1, 4), (8, 5, 1, 11),
    (9, 14, 1, 4), (10, 14, 1, 4);

INSERT INTO `rating` (`score`, `recipe`) VALUES
    (5, 1), (5, 1), (5, 2), (5, 2), (5, 3), (5, 3), (5, 3), (5, 4), (5, 5), (5, 6), (5, 7), (5, 9), (5, 10), (5, 10),
    (4, 1), (4, 1), (4, 1), (4, 2), (4, 2), (4, 2), (4, 3), (4, 3), (4, 4), (4, 4), (4, 6), (4, 6), (4, 6), (4, 7),
    (4, 8), (4, 8), (4, 9), (3, 1), (3, 2), (3, 2), (3, 2), (3, 2), (3, 2), (3, 3), (3, 3), (3, 3), (3, 4), (3, 5),
    (3, 6), (3, 6), (3, 7), (3, 7), (3, 7), (3, 7), (3, 8), (3, 9), (2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6),
    (2, 7), (2, 8), (2, 9), (2, 10), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 2), (1, 3), (1, 3), (1, 3), (1, 3),
    (1, 3), (1, 4), (1, 4), (1, 4), (1, 4), (1, 5), (1, 6), (1, 7), (1, 7), (1, 8), (1, 8), (1, 8), (1, 9), (1, 9),
    (1, 10), (1, 10), (1, 10);
