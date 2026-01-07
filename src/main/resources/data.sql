-- Sample movie data with external image URLs (using individual inserts for better compatibility)

-- Movie 1
INSERT INTO movie (title, director, release_date, duration_minutes, genre, description, image_path) 
SELECT 'The Shawshank Redemption', 'Frank Darabont', '1994-09-23', 142, 'Drama', 'Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.', 'https://image.tmdb.org/t/p/w500/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = 'The Shawshank Redemption');

-- Movie 2
INSERT INTO movie (title, director, release_date, duration_minutes, genre, description, image_path) 
SELECT 'The Godfather', 'Francis Ford Coppola', '1972-03-24', 175, 'Crime, Drama', 'The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.', 'https://image.tmdb.org/t/p/w500/3bhkrj58Vtu7enYsRolD1fZdja1.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = 'The Godfather');

-- Movie 3
=======
>>>>>>> feature-app
WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = 'The Dark Knight');

-- Movie 4
INSERT INTO movie (title, director, release_date, duration_minutes, genre, description, image_path) 
SELECT 'Pulp Fiction', 'Quentin Tarantino', '1994-10-14', 154, 'Crime, Drama', 'The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.', 'https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = 'Pulp Fiction');

-- Movie 5
INSERT INTO movie (title, director, release_date, duration_minutes, genre, description, image_path) 
<<<<<<< HEAD
SELECT 'Schindler''s List', 'Steven Spielberg', '1993-12-15', 195, 'Biography, Drama, History', 'In German-occupied Poland during World War II, industrialist Oskar Schindler gradually becomes concerned for his Jewish workforce after witnessing their persecution by the Nazis.', 'schindlers_list.jpg'
=======
SELECT 'Schindler''s List', 'Steven Spielberg', '1993-12-15', 195, 'Biography, Drama, History', 'In German-occupied Poland during World War II, industrialist Oskar Schindler gradually becomes concerned for his Jewish workforce after witnessing their persecution by the Nazis.', 'https://image.tmdb.org/t/p/w500/sF1U4EUQS8YHUYjNl3pMGNIQyr0.jpg'
-- Movie 6
<<<<<<< HEAD
SELECT 'Inception', 'Christopher Nolan', '2010-07-16', 148, 'Action, Adventure, Sci-Fi', 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', 'inception.jpg'
=======
SELECT 'Inception', 'Christopher Nolan', '2010-07-16', 148, 'Action, Adventure, Sci-Fi', 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', 'https://image.tmdb.org/t/p/w500/8IB2e4r4oVhHnANbnm7O3Tj6tF8.jpg'
>>>>>>> feature-app
WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = 'Inception');

-- Movie 7
INSERT INTO movie (title, director, release_date, duration_minutes, genre, description, image_path) 
<<<<<<< HEAD
SELECT 'Fight Club', 'David Fincher', '1999-10-15', 139, 'Drama', 'An insomniac office worker and a devil-may-care soapmaker form an underground fight club that evolves into something much, much more.', 'fight_club.jpg'
=======
SELECT 'Fight Club', 'David Fincher', '1999-10-15', 139, 'Drama', 'An insomniac office worker and a devil-may-care soapmaker form an underground fight club that evolves into something much, much more.', 'https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg'
>>>>>>> feature-app
WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = 'Fight Club');

-- Movie 8
INSERT INTO movie (title, director, release_date, duration_minutes, genre, description, image_path) 
<<<<<<< HEAD
SELECT 'Forrest Gump', 'Robert Zemeckis', '1994-07-06', 142, 'Drama, Romance', 'The presidencies of Kennedy and Johnson, the events of Vietnam, Watergate, and other historical events unfold through the perspective of an Alabama man with an IQ of 75.', 'forrest_gump.jpg'
=======
SELECT 'Forrest Gump', 'Robert Zemeckis', '1994-07-06', 142, 'Drama, Romance', 'The presidencies of Kennedy and Johnson, the events of Vietnam, Watergate, and other historical events unfold through the perspective of an Alabama man with an IQ of 75.', 'https://image.tmdb.org/t/p/w500/arw2vcBveWOVZr6pxd9XTd1TdQa.jpg'
>>>>>>> feature-app
WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = 'Forrest Gump');

-- Movie 9
INSERT INTO movie (title, director, release_date, duration_minutes, genre, description, image_path) 
<<<<<<< HEAD
SELECT 'The Matrix', 'Lana Wachowski, Lilly Wachowski', '1999-03-31', 136, 'Action, Sci-Fi', 'A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.', 'matrix.jpg'
=======
SELECT 'The Matrix', 'Lana Wachowski, Lilly Wachowski', '1999-03-31', 136, 'Action, Sci-Fi', 'A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.', 'https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg'
>>>>>>> feature-app
WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = 'The Matrix');

-- Movie 10
INSERT INTO movie (title, director, release_date, duration_minutes, genre, description, image_path) 
<<<<<<< HEAD
SELECT 'Interstellar', 'Christopher Nolan', '2014-11-07', 169, 'Adventure, Drama, Sci-Fi', 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity''s survival.', 'interstellar.jpg'
=======
SELECT 'Interstellar', 'Christopher Nolan', '2014-11-07', 169, 'Adventure, Drama, Sci-Fi', 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity''s survival.', 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg'
>>>>>>> feature-app
WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = 'Interstellar');
