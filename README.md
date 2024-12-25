# java-filmorate
Template repository for the Filmorate project.

## Database Diagram

![Database Diagram](photo/Untitled.png)

## Example Query

### Adding a New Film to the `films` Table

```sql

INSERT INTO films (name, description, release_date, duration)
VALUES ('The Lord of the Rings', 'A fantasy movie about the adventures of hobbits', '2001-12-19', 178);

Getting All Films

SELECT * FROM films;

Getting All Users

SELECT * FROM users;

Top Films by Number of Likes

SELECT f.id, f.name, COUNT(l.user_id) AS likes_count
FROM films f
JOIN likes l ON f.id = l.film_id
GROUP BY f.id, f.name
ORDER BY likes_count DESC;


Getting a list of all movies that were liked by more than 10 users

SELECT f.id, f.name, COUNT(l.user_id) AS likes_count
FROM films f
JOIN likes l ON f.id = l.film_id
GROUP BY f.id
HAVING COUNT(l.user_id) > 10
ORDER BY likes_count DESC;


Obtaining films that were liked by the most active users (by number of likes), indicating the number of likes and the genre of the film

SELECT 
    f.id AS film_id,
    f.name AS film_name,
    COUNT(l.user_id) AS likes_count,
    g.name AS genre
FROM films f
JOIN likes l ON f.id = l.film_id
JOIN film_genres fg ON f.id = fg.film_id
JOIN genres g ON fg.genre_id = g.id
GROUP BY f.id, f.name, g.name
HAVING COUNT(l.user_id) > 5 
ORDER BY likes_count DESC;
