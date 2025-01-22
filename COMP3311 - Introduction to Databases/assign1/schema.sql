-- Tables

create table Countries (
	id          integer,  -- would normally use serial in PostgreSQL
	code        char(3) not null,
	name        text not null,
	primary key (id)
);

create table Locations (
	id          integer,  -- would normally use serial in PostgreSQL
	within      integer  not null references Countries(id),
	region      text,  -- state or shire or ...
	metro       text,  -- metroploitan area (e.g. Sydney)
	town        text,  -- in metro area => suburb, outside metro => town
	primary key (id)
);

create table Styles (
	id          integer,  -- would normally use serial in PostgreSQL
	name        text not null,  -- name of style (e.g. lager, IPA)
	min_abv     ABVvalue,
	max_abv     ABVvalue,
	primary key (id)
);

create table Ingredients (
	id          integer,  -- would normally use serial in PostgreSQL
	itype       IngredientType not null,
	name        text not null,
	origin      integer references Countries(id),
	primary key (id)
);

create table Breweries (
	id          integer,  -- would normally use serial in PostgreSQL
	name        text not null,
	founded     YearValue,
	website     URLvalue,
	located_in  integer not null references Locations(id),
	primary key (id)
);

create table Beers (
	id          integer,  -- would normally use serial in PostgreSQL
	name        text not null,
	vintage     YearValue,          -- year brewed
	style       integer not null references Styles(id),
	ABV         ABVvalue not null,  -- strength
	IBU         IBUvalue,           -- bitterness
	sold_in     ContainerType,
	volume      MilliLiters,
	notes       text,
	rating      integer not null check (rating between 0 and 10),
	primary key (id)
);

create table Contains (
	beer        integer references Beers(id),
	ingredient  integer references Ingredients(id),
	primary key (beer,ingredient)
);

create table Brewed_by (
	beer        integer references Beers(id),
	brewery     integer references Breweries(id),
	primary key (beer,brewery)
);