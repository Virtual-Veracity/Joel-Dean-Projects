-- COMP3311 23T1 Assignment 1
-- By Joel Dean (z5208947)

-- Q1: amount of alcohol in the best beers

-- Single View
-- From Beers Table
-- Extract all beers with a rating greater than 9
-- Extract all information needed from Beers table

CREATE OR REPLACE VIEW Q1(beer, "sold in", alcohol)
AS
SELECT name, CONCAT(volume, 'ml ', sold_in), CONCAT(((volume * ABV) / 100)::numeric(4,1), 'ml')
FROM Beers
WHERE rating > 9
;

-- Q2: beers that don't fit the ABV style guidelines

-- Helper Function: reason()
-- Create text for the reason return attribute (In Q2)

CREATE OR REPLACE FUNCTION 
	Reason(beerABV ABVvalue, minABV ABVvalue, maxABV ABVvalue) RETURNS TEXT
AS
$$
DECLARE
	offBy NUMERIC(4,1);
BEGIN 

	IF beerABV < minABV
	THEN
		offby = (minABV - beerABV);
		RETURN 'too weak by ' || offby ||'%';
	ELSE
		offby = (beerABV - maxABV);
		RETURN 'too strong by ' || offby ||'%';
	END IF;

END; 
$$ LANGUAGE plpgsql
;

-- Join Beers and Style Table
-- For all beers that are outdated (ABV not in respective style range)
-- Give the needed info on beer
-- Utilise reason function to create the reason attribute

CREATE OR REPLACE VIEW Q2(beer, style, abv, reason) 
AS
SELECT B.name, S.name, B.ABV::ABVvalue, Reason(B.ABV, S.min_abv, S.max_abv)
FROM Beers as B
Join Styles as S ON B.style = S.id
WHERE B.ABV > S.max_abv OR B.ABV < S.min_abv
;

-- Q3: Number of beers brewed in each country
-- Joins all appropriate Tables
-- Uses FULL OUTER JOIN 
-- Ensures no country is missed

CREATE OR REPLACE VIEW Q3(country, "#beers")
AS
SELECT C.name, COUNT(Beers.id)::bigint
FROM Brewed_by AS Brew 
FULL OUTER JOIN Beers ON Beers.id = Brew.beer
FULL OUTER JOIN Breweries ON Breweries.id = Brew.brewery 
FULL OUTER JOIN Locations AS L ON L.id = Breweries.located_in
FULL OUTER JOIN Countries AS C ON C.id = L.within
GROUP BY C.id
;

-- Q4: Countries where the worst beers are brewed
-- For all Beers with a rating less than 3
-- Return appropriate info

CREATE OR REPLACE VIEW Q4(beer, brewery, country)
AS
SELECT Beers.name, Breweries.name, C.name
FROM Brewed_by AS Brew 
JOIN Beers ON Beers.id = Brew.beer
JOIN Breweries ON Breweries.id = Brew.brewery 
JOIN Locations AS L ON L.id = Breweries.located_in
JOIN Countries AS C ON C.id = L.within
WHERE Beers.rating < 3
;

-- Q5: Beers that use ingredients from the Czech Republic

CREATE OR REPLACE VIEW Q5(beer, ingredient, "type")
AS
SELECT Beers.name, I.name, I.itype::IngredientType 
FROM Contains
JOIN Beers ON Beers.id = Contains.beer
JOIN Ingredients AS I On I.id = Contains.ingredient
JOIN Countries AS C ON C.id = I.origin
WHERE C.name = 'Czech Republic'
;

-- Q6: Beers containing the most used hop and the most used grain

-- Helper View: HopCount()
-- View returns individual hop count in beers (Descending order)
CREATE OR REPLACE VIEW HopCount(hopName, hopID, hCount) 
AS
SELECT I.name, I.id, COUNT(I.id) AS hCount
FROM Contains
JOIN Beers ON Beers.id = Contains.beer
JOIN Ingredients AS I On I.id = Contains.ingredient
WHERE I.itype = 'hop'
GROUP BY I.id
ORDER BY hCount DESC
;

-- Helper View: GrainCount()
-- View returns individual grain count in beers (Descending order)
CREATE OR REPLACE VIEW GrainCount(grainName, grainID, gCount) 
AS
SELECT I.name, I.id, COUNT(I.id) AS gCount
FROM Contains
JOIN Beers ON Beers.id = Contains.beer
JOIN Ingredients AS I On I.id = Contains.ingredient
WHERE I.itype = 'grain'
GROUP BY I.id
ORDER BY gCount DESC
;



-- Question 6
-- Find Beers with both the most popular hop/s and the most popular grain/s
-- If 2+ hops are found with the same top popularity -> Use BOTH
-- If 2+ grains are found with the same top popularity -> Use BOTH

-- Part A
-- Limit beer selection to those with at least one popular ingedient

-- Part B
-- Ensure the beers displayed contain ALL popular ingredients (hops/grains)
--            1. Find the Number of grains with top usage (E.g. Spelt 20, Wheat 20 -> 2 Grains)
--			  2. Find a similar value for hops ingredients (E.g. Pioneer 32 -> 1 Hop)
--            3. The sum of these 2 values is the number of hops/grain ingredients a beer needs to be displayed

CREATE OR REPLACE VIEW Q6(beer)
AS
SELECT Beers.name
FROM Beers
JOIN Contains ON Contains.beer = Beers.id 
JOIN Ingredients AS I ON I.id = Contains.ingredient
WHERE I.id IN (SELECT hopID FROM HopCount WHERE hCount =
														(SELECT MAX(hCount) FROM hopCount))
OR    I.id IN (SELECT grainID FROM grainCount WHERE gCount =
														(SELECT MAX(gCount) FROM grainCount))
GROUP BY Beers.id 
HAVING COUNT(Beers.id) = (SELECT COUNT(hopID) FROM hopCount GROUP BY hCount ORDER BY hCount DESC LIMIT 1) + 
						 (SELECT COUNT(grainID) FROM grainCount GROUP BY gCount ORDER BY gCount DESC LIMIT 1)
;

-- Q7: Breweries that make no beer
-- Find a Count of beer per breweries
-- Display all breweries with a count of 0

CREATE OR REPLACE VIEW Q7(brewery)
AS
SELECT Brewer.name
FROM Breweries AS Brewer
FULL OUTER JOIN Brewed_by ON Brewed_by.brewery = Brewer.id
GROUP BY Brewer.id
HAVING COUNT(Brewed_by.beer) = 0
;

-- Question 8

-- Helper Function: PretentiousPeople(BeerID)
-- Returns a Set Of BrewerNames (Alphabetical Order)
CREATE OR REPLACE FUNCTION 
	PretentiousPeople(Beers.id%Type) RETURNS SETOF Breweries.name%TYPE AS
$$
SELECT DISTINCT Breweries.name 
FROM Brewed_by 
JOIN Beers ON Beers.id = Brewed_by.beer
JOIN Breweries ON Breweries.id = Brewed_by.brewery
WHERE Beers.id = $1
ORDER BY Breweries.name
$$ LANGUAGE sql
;

-- Q8: Function to give "full name" of beer
-- Create Full Name of a given beer
-- Maximum of 2 Breweries returned (First 2 in alphabetical order)
-- Pass through specified Regular Expression
CREATE OR REPLACE FUNCTION
	Q8(beer_id INTEGER) returns TEXT
AS
$$
DECLARE 
	beerFullName TEXT = '';
	currentBrewery TEXT;
	originalBrewery TEXT;
	breweryCount INTEGER = 0;
	beerName TEXT = (SELECT Beers.name
					 FROM Beers 
					 WHERE Beers.id = beer_id);
BEGIN
	-- Invalid Beer
	IF beerName IS NULL
	THEN
		RETURN 'No such beer';
	END IF;

	FOR currentBrewery IN SELECT * FROM PretentiousPeople(beer_id) 
	LOOP
		-- If second brewery exists add a '+'
		IF breweryCount = 1
		THEN 
			beerFullName = beerFullName || ' + ';
		END IF;

		-- Already added 2 brewers already (Chosen by Alphabetical Order)	
		IF breweryCount >= 2
		THEN
			EXIT;
		END IF;

		-- Regex to get desired text
		originalBrewery = currentBrewery;
		currentbrewery = REGEXP_REPLACE(currentBrewery, ' (Beer|Brew).*$', '');

		-- Regex returns an empty string
		IF currentBrewery = ''
		THEN 
			currentBrewery = originalBrewery;
		END IF;

		beerFullName = beerFullName || currentBrewery;
		breweryCount = breweryCount + 1;
	END LOOP;

	beerFullName = beerFullName || ' ' || beerName;
	RETURN beerFullName;
END;
$$ LANGUAGE plpgsql
;

-- Question 9

drop type if exists BeerData cascade;
create type BeerData as (beer text, brewer text, info text);

-- Helper Function: matchString(TEXT)
-- Returns all beerID's whose beerNames contain the input TEXT
CREATE OR REPLACE FUNCTION 
	matchString(TEXT) RETURNS SETOF INTEGER AS 
$$
SELECT Beers.id
FROM Beers
WHERE Beers.name ~* CONCAT('.*', $1, '.*')
$$ LANGUAGE sql 
;

-- Helper Function: getBeerName(BeerID)
-- Return the name associated with a beerID
CREATE OR REPLACE FUNCTION 
	getBeerName(INTEGER) RETURNS TEXT AS
$$
	SELECT Beers.name
	FROM Beers
	WHERE Beers.id = $1
$$ LANGUAGE sql;

-- Helper Function: getBeerBrewers(BeerID)
-- Return the brewers associated with a beerID
-- Separate brewers by a '+'
CREATE OR REPLACE FUNCTION 
	getBeerBrewers(beerID INTEGER) RETURNS TEXT AS
$$
DECLARE 
	breweryCount INTEGER = 0; 
	beerFullName TEXT = '';
	currentBrewery TEXT;
BEGIN
	FOR currentBrewery IN SELECT * FROM PretentiousPeople(beerid) 
	LOOP
		-- If second brewery exists add a '+'
		IF breweryCount >= 1
		THEN 
			beerFullName = beerFullName || ' + ';
		END IF;

		beerFullName = beerFullName || currentBrewery;
		breweryCount = breweryCount + 1;
	END LOOP;
	RETURN beerFullName;
END;
$$ LANGUAGE plpgsql;

-- Helper Function: getBeerIngredients(BeerID)
-- Create a string for the info return attribute
CREATE OR REPLACE FUNCTION 
	getBeerIngredients(beerID INTEGER) RETURNS TEXT AS 
$$
DECLARE 
	beerIngredient TEXT;
	beerType TEXT;
	beerInfo TEXT;
	hopsInfo TEXT  = '';
	grainInfo TEXT = '';
	extraInfo TEXT = '';
	hCount INTEGER = 0;
	gCount INTEGER = 0;
	eCount INTEGER = 0;
BEGIN 
	
	-- Loop through Ingredients Table
	FOR beerIngredient, beerType IN (SELECT Ingredients.name, Ingredients.itype
						   			 FROM Contains
									 JOIN Ingredients ON Contains.ingredient = Ingredients.id 
						   			 WHERE Contains.beer = beerID)
	LOOP
		IF beerType = 'hop'
		THEN 
			IF hCount = 0
			THEN 
				hopsInfo = 'Hops: ' || beerIngredient;
			ELSE
				hopsInfo = hopsInfo || ',' || beerIngredient;
			END IF;
			hCount = hCount + 1;
		END IF;
		IF beerType = 'grain'
		THEN 
			IF gCount = 0
			THEN 
				grainInfo = 'Grain: ' || beerIngredient;
			ELSE
				grainInfo = grainInfo || ',' || beerIngredient;
			END IF;
			gCount = gCount + 1;
		END IF;
		IF beerType = 'adjunct'
		THEN 
			IF eCount = 0
			THEN 
				extraInfo = 'Extras: ' || beerIngredient;
			ELSE
				extraInfo = extraInfo || ',' || beerIngredient;
			END IF;
			eCount = eCount + 1;
		END IF;
	END LOOP;

	-- Insert newlines, Where necessary
	IF hopsInfo != '' AND gCount + eCount != 0
	THEN 
		hopsInfo = hopsInfo || E'\n';
	END IF;
	IF grainInfo != '' AND eCount != 0
	THEN 
		grainInfo = grainInfo || E'\n';
	END IF;

	beerInfo = hopsInfo || grainInfo || extraInfo;
	RETURN beerInfo;
END; 
$$ LANGUAGE plpgsql;

-- Q9: Beer data based on partial match of beer name
-- Find beers whose name matches contains input TEXT (Case Insensitive)
-- Call Helper Functions to fill BeerData Return Type
CREATE OR REPLACE FUNCTION
	Q9(partial_name TEXT) RETURNS SETOF BeerData
AS
$$
DECLARE 
	beerID INTEGER;
	beerDisplay BeerData;
BEGIN
	FOR beerID IN SELECT * FROM matchString(partial_name)
	LOOP 
		beerDisplay.beer   = getBeerName(beerID);
		beerDisplay.brewer = getBeerBrewers(beerID);
		beerDisplay.info   = getBeerIngredients(beerID);
		RETURN NEXT beerDisplay;
	END LOOP;
	RETURN;
END;
$$ LANGUAGE plpgsql
;

