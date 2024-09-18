create extension hstore;
create schema pizzeria;
create table if not exists pizzeria."Pizza" ("pizza_id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"price" INTEGER NOT NULL,"n_ingredients" INTEGER NOT NULL);
create table if not exists pizzeria."Ingredient" ("ingredient_id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL);
create table if not exists pizzeria."PizzaIngredientMapping" ("pizza_ingredient_id" BIGSERIAL NOT NULL PRIMARY KEY,"pizza_id" BIGINT NOT NULL,"ingredient_id" BIGINT NOT NULL);