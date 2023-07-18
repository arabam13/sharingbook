--DROP TABLES
-- drop table Borrow;
-- drop table Book;
-- drop table Category;
-- drop table User_info;

-- UserInfo
create table IF NOT EXISTS User_info(
    id serial primary key,
    email varchar(30) not null,
    first_name varchar(25) not null,
    last_name varchar(25) not null,
    password varchar(250) not null
);

-- Category
create table IF NOT EXISTS Category(
    id serial primary key,
    label varchar(50) not null
);

--Type BookStatus
-- CREATE TYPE Book_status AS ENUM('FREE', 'BORROWED');

-- Book
create table IF NOT EXISTS Book (
    id serial primary key ,
    title varchar(255) not null,
    -- status Book_status default 'FREE',
    status varchar(8) default 'FREE',
    author_id int,
    foreign key (author_id) references User_info(id),
    category_id int,
    foreign key (category_id) references Category(id)
);

-- Borrow
create table IF NOT EXISTS Borrow(
    id serial primary key ,
    ask_date timestamp,
    close_date timestamp,
    book_id int,
    foreign key (book_id) references Book(id),
    lender_id int,
    foreign key (lender_id) references User_info(id),
    borrower_id int,
    foreign key (borrower_id) references User_info(id)
);

--Category

-- INSERT INTO category (label) VALUES ('Roman');
-- INSERT INTO category (label) VALUES ('BD');
-- INSERT INTO category (label) VALUES ('Essai');
-- INSERT INTO category (label) VALUES ('Cuisine');
-- INSERT INTO category (label) VALUES ('Bricolage');
-- INSERT INTO category (label) VALUES ('Enfant');
-- INSERT INTO category (label) VALUES ('Sciences');
