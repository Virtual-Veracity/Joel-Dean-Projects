-- COMP3311 T1 2023
-- Assignment 2 (Due 14th April)
-- Joel Dean (z5208947)

-- Contains SQL Views/Functions to reference from helpers.py

-- COMP3311 23T1 Ass2 ... SQL helper Views/Functions
-- Add any views or functions you need into this file
-- Note: it must load without error into a freshly created Movies database
-- Note: you must submit to his file even if you add nothing to it

-- The `dbpop()` function is provided for you in the dump file
-- This is provided in case you accidentally delete it

DROP TYPE IF EXISTS Population_Record CASCADE;
CREATE TYPE Population_Record AS (
	Tablename Text,
	Ntuples   Integer
);

CREATE OR REPLACE FUNCTION DBpop() RETURNS SETOF Population_Record
AS $$
DECLARE
    rec Record;
    qry Text;
    res Population_Record;
    num Integer;
BEGIN
    FOR rec IN SELECT tablename FROM pg_tables WHERE schemaname='public' ORDER BY tablename LOOP
        qry := 'SELECT count(*) FROM ' || quote_ident(rec.tablename);

        execute qry INTO num;

        res.tablename := rec.tablename;
        res.ntuples   := num;

        RETURN NEXT res;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

--
-- Example Views/Functions
-- These Views/Functions may or may not be useful to you.
-- You may modify or delete them as you see fit.
--

-- `Move_Learning_Info`
-- The `Learnable_Moves` table is a relation between Pokemon, Moves, Games and Requirements.
-- As it just consists of foreign keys, it is not very easy to read.
-- This view makes it easier to read by displaying the names of the Pokemon, Moves and Games instead of their IDs.
CREATE OR REPLACE VIEW Move_Learning_Info(Pokemon, Move, Game, Requirement) AS
SELECT
    P.Name,
    M.Name,
    G.Name,
    R.Assertion
FROM
    Learnable_Moves AS L
    JOIN
    Pokemon         AS P ON Learnt_By   = P.ID
    JOIN
    Games           AS G ON Learnt_In   = G.ID
    JOIN
    Moves           AS M ON Learns      = M.ID
    JOIN
    Requirements    AS R ON Learnt_When = R.ID
;

-- `Super_Effective`
-- This function takes a type name and
-- returns a set of all types that it is super effective against (multiplier > 100)
-- eg Water is super effective against Fire, so `Super_Effective('Water')` will return `Fire` (amongst others)
CREATE OR REPLACE FUNCTION Super_Effective(_Type Text) RETURNS SETOF Text
AS $$
SELECT
    B.Name
FROM
    Types              AS A
    JOIN
    Type_Effectiveness AS E ON A.ID = E.Attacking
    JOIN
    Types              AS B ON B.ID = E.Defending
WHERE
    A.Name = _Type
    AND
    E.Multiplier > 100
$$ LANGUAGE SQL;

--
-- Your Views/Functions Below Here
-- Remember This file must load into a clean Pokemon database in one pass without any error
-- NOTICEs are fine, but ERRORs are not
-- Views/Functions must be defined in the correct order (dependencies first)
-- eg if my_supper_clever_function() depends on my_other_function() then my_other_function() must be defined first
-- Your Views/Functions Below Here
--

CREATE OR REPLACE FUNCTION 
    AttackDefenceValue(category TEXT, pokemonAttacker TEXT, pokemonDefender TEXT) RETURNS SETOF INT AS
$$
BEGIN 
    IF category = 'Special'
    THEN 
        RETURN NEXT (SELECT (Base_Stats).Special_Attack FROM Pokemon WHERE Name = pokemonAttacker);
        RETURN NEXT (SELECT (Base_Stats).Special_Defense FROM Pokemon WHERE Name = pokemonDefender);
    ELSIF category = 'Status' 
    THEN 
        RETURN NEXT 0;
        RETURN NEXT 0;
    ELSE
        RETURN NEXT (SELECT (Base_Stats).Attack FROM Pokemon WHERE Name = pokemonAttacker);
        RETURN NEXT (SELECT (Base_Stats).Defense FROM Pokemon WHERE Name = pokemonDefender);
    END IF;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION
    SecondType(pokemonName TEXT) RETURNS TEXT AS
$$
DECLARE 
    secondType TEXT;
BEGIN 
    secondType = (SELECT Types.Name
                  FROM Pokemon 
                  JOIN Types ON Types.ID = Pokemon.Second_Type
                  WHERE pokemonName = Pokemon.Name);

    IF secondType IS NULL
    THEN 
        secondType = '';
    END IF;
    RETURN secondType;
END
$$ LANGUAGE plpgsql;
