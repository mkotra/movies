package pl.mkotra.movies.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/populate-data")
public class PopulateDataController {

    private final JdbcTemplate jdbcTemplate;

    public PopulateDataController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/populate")
    void populateData() {
        //TODO; This should be replaced with data load from files.

        jdbcTemplate.execute("DELETE FROM appearances");
        jdbcTemplate.execute("DELETE FROM actors");
        jdbcTemplate.execute("DELETE FROM movies");

        jdbcTemplate.execute("""
            INSERT INTO movies (id, title, year) VALUES
                            (1, 'The Silent Echo', 1998),
                            (2, 'Beyond the Horizon', 2005),
                            (3, 'Shadow of the Past', 2012),
                            (4, 'Neon Nights', 2021),
                            (5, 'Crimson Legacy', 1994),
                            (6, 'Fading Memories', 2018),
                            (7, 'Infinite Loop', 2020),
                            (8, 'The Last Voyage', 2003),
                            (9, 'Echoes of Time', 2017),
                            (10, 'Blackout Protocol', 2015),
                            (11, 'Under the Moon', 2009),
                            (12, 'Whispers in the Dark', 2001),
                            (13, 'The Forgotten Realm', 2013),
                            (14, 'Redemption Road', 2008),
                            (15, 'Chasing the Storm', 2022),
                            (16, 'City of Secrets', 2010),
                            (17, 'The Midnight Caller', 2016),
                            (18, 'Frozen Destiny', 1999),
                            (19, 'Cybernetic Dreams', 2023),
                            (20, 'End of the Line', 2014),
                            (21, 'The Last Chance', 2007),
                            (22, 'Shattered Illusions', 2021),
                            (23, 'Tomorrow’s Dawn', 1995),
                            (24, 'Echo Chamber', 2019),
                            (25, 'Dark Waters', 2011),
                            (26, 'The Mechanic’s Code', 2000),
                            (27, 'Quantum Paradox', 2023),
                            (28, 'Deadly Mirage', 2004),
                            (29, 'Serpent’s Curse', 2015),
                            (30, 'Celestial Path', 2018),
                            (31, 'No Way Out', 2009),
                            (32, 'Terminal Point', 2022),
                            (33, 'Wings of Fate', 2016),
                            (34, 'The Hidden Agenda', 2013),
                            (35, 'Beneath the Ashes', 2006),
                            (36, 'Vortex Rising', 2020),
                            (37, 'The Shadow Code', 1997),
                            (38, 'Neon Assassin', 2011),
                            (39, 'Lunar Sanctuary', 2019),
                            (40, 'Cobalt Skies', 2002),
                            (41, 'Gilded Lies', 2008),
                            (42, 'After the Storm', 2015),
                            (43, 'Silent Threat', 2017),
                            (44, 'Echoes in the Void', 2021),
                            (45, 'Reckoning Night', 2003),
                            (46, 'Phantom Protocol', 2014),
                            (47, 'Vanishing Point', 2020),
                            (48, 'The Steel City', 2012),
                            (49, 'Last Light', 2005),
                            (50, 'Final Descent', 2023);
        """);

        jdbcTemplate.execute("""
                INSERT INTO actors (id, name) VALUES
                            (1, 'Ethan Carter'),
                            (2, 'Sophia Ramirez'),
                            (3, 'Jack Nolan'),
                            (4, 'Isabella Monroe'),
                            (5, 'Daniel Foster'),
                            (6, 'Ava Sinclair'),
                            (7, 'Henry Lawson'),
                            (8, 'Mia Thompson'),
                            (9, 'Noah Blake'),
                            (10, 'Emily Richards'),
                            (11, 'Liam Harrington'),
                            (12, 'Olivia Bennett'),
                            (13, 'Benjamin Cross'),
                            (14, 'Emma Scott'),
                            (15, 'James Caldwell'),
                            (16, 'Hannah Pierce'),
                            (17, 'Lucas Reed'),
                            (18, 'Madeline Carter'),
                            (19, 'Nathan Drake'),
                            (20, 'Claire Davidson');
        """);

        jdbcTemplate.execute(""" 
                INSERT INTO appearances (movie_id, actor_id, character_name) VALUES
                            (1, 1, 'Detective Cole'),
                            (2, 2, 'Dr. Evelyn Harper'),
                            (3, 3, 'Michael Trent'),
                            (4, 4, 'Samantha Greer'),
                            (5, 5, 'Victor Langley'),
                            (6, 6, 'Serena Vaughn'),
                            (7, 7, 'James Walker'),
                            (8, 8, 'Natalie Brooks'),
                            (9, 9, 'Ethan Grayson'),
                            (10, 10, 'Clara West'),
                            (11, 11, 'Leon Everett'),
                            (12, 12, 'Amelia Clarke'),
                            (13, 13, 'Gabriel Steele'),
                            (14, 14, 'Isabel Thornton'),
                            (15, 15, 'Marcus Holloway'),
                            (16, 16, 'Eliza Kane'),
                            (17, 17, 'Samuel Vance'),
                            (18, 18, 'Rebecca Cole'),
                            (19, 19, 'Julian Price'),
                            (20, 20, 'Charlotte Hale'),
                            (21, 1, 'Derek Vaughn'),
                            (22, 2, 'Lucia Carter'),
                            (23, 3, 'Theo Morgan'),
                            (24, 4, 'Riley Bennett'),
                            (25, 5, 'Vincent Clarke'),
                            (26, 6, 'Eva Sinclair'),
                            (27, 7, 'Adam Turner'),
                            (28, 8, 'Sophia Knight'),
                            (29, 9, 'Nathan Cross'),
                            (30, 10, 'Fiona Lane'),
                            (31, 11, 'Adrian Knox'),
                            (32, 12, 'Cassandra Monroe'),
                            (33, 13, 'Harrison Wells'),
                            (34, 14, 'Aurora Hayes'),
                            (35, 15, 'Caleb Foster'),
                            (36, 16, 'Victoria Mason'),
                            (37, 17, 'Damian Rhodes'),
                            (38, 18, 'Liliana Pierce'),
                            (39, 19, 'Owen Baxter'),
                            (40, 20, 'Celeste Jordan'),
                            (41, 1, 'Jasper Lane'),
                            (42, 2, 'Miranda Shaw'),
                            (43, 3, 'Grayson Bennett'),
                            (44, 4, 'Eleanor King'),
                            (45, 5, 'Isaac Vaughn'),
                            (46, 6, 'Diana Carter'),
                            (47, 7, 'Sebastian Knight'),
                            (48, 8, 'Luna Sinclair'),
                            (49, 9, 'Maxwell Hale'),
                            (50, 10, 'Scarlett Evans');
                """
        );
    }
}
