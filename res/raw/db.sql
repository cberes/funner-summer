CREATE TABLE IF NOT EXISTS pastime (
_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
name TEXT NOT NULL UNIQUE COLLATE NOCASE,
action_name TEXT DEFAULT NULL,
locale TEXT DEFAULT NULL,
active INTEGER NOT NULL DEFAULT 1,
custom INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS pastime_alias (
_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
pastime_id INTEGER NOT NULL REFERENCES pastime(_id),
name TEXT NOT NULL COLLATE NOCASE
);

CREATE TABLE IF NOT EXISTS selection_method (
_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
name TEXT NOT NULL UNIQUE COLLATE NOCASE
);

CREATE TABLE IF NOT EXISTS action (
_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
pastime_id INTEGER NOT NULL REFERENCES pastime(_id),
method_id INTEGER NOT NULL REFERENCES selection_method(_id),
performed INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP,
day INTEGER NOT NULL DEFAULT (CAST (strftime('%w', date('now'), 'localtime') AS INTEGER)) COLLATE NOCASE
);

CREATE TABLE IF NOT EXISTS statistic (
_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
name TEXT NOT NULL UNIQUE COLLATE NOCASE,
type TEXT NOT NULL COLLATE NOCASE
);

CREATE TABLE IF NOT EXISTS measurement (
_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
stat_id INTEGER NOT NULL REFERENCES statistic(_id),
action_id INTEGER NOT NULL REFERENCES action(_id),
value_integer INTEGER,
value_text TEXT COLLATE NOCASE
);

INSERT INTO selection_method
(name) VALUES
('ballast'),
('historical'),
('inactive'),
('random'),
('manual');

INSERT INTO statistic
(name, type) VALUES
('temperature', 'integer'),
('weather', 'text'),
('crowd', 'text');

INSERT INTO pastime
(name, action_name, custom) VALUES
('running', 'Go running', 0),
('walking', 'Take a walk', 0),
('hiking', 'Take a hike', 0),
('cycling', 'Enjoy a bike ride', 0),
('mountain biking', 'Go mountain biking', 0),
('orienteering', 'Go orienteering', 0),
('catch', 'Play catch', 0),
('swimming', 'Go swimming', 0),
('beach', 'Go to a beach', 0),
('park', 'Go to a park', 0),
('picnic', 'Go on a picnic', 0),
('painting', 'Draw or paint a picture', 0),
('reading', 'Read a book', 0),
('letter', 'Write a letter to a friend', 0),
('pet', 'Volunteer at a pet shelter', 0),
('soup', 'Volunteer at a soup kitchen', 0),
('elderly', 'Read to elderly people', 0),
('zoo', 'Visit a zoo', 0),
('golfing', 'Play a round of golf', 0),
('disc golfing', 'Play a round of disc golf', 0),
('mini golf', 'Play a round of miniature golf', 0),
('tennis', 'Play tennis', 0),
('badminton', 'Play badminton', 0),
('volleyball', 'Play volleyball', 0),
('movie', 'Watch a movie', 0),
('drive-in', 'Catch a movie at a drive-in', 0),
('softball', 'Join a softball league', 0),
('driving', 'Enjoy a road trip', 0),
('skateboarding', 'Go skateboarding', 0),
('roller skating', 'Go roller skating', 0),
('surfing', 'Go surfing', 0),
('tubing', 'Go tubing on a river', 0),
('ice skating', 'Go ice skating', 0),
('snowboarding', 'Go snowboarding', 0),
('snow tubing', 'Go snow tubing', 0),
('skiing downhill', 'Go downhill skiing', 0),
('skydiving', 'Try skydiving', 0),
('ballooning', 'Enjoy a hot-air balloon ride', 0),
('bungee jumping', 'Try bungee jumping', 0),
('zip lining', 'Try zip lining', 0),
('kayaking', 'Enjoy a kayak ride', 0),
('fishing', 'Go fishing', 0),
('carnival', 'Visit a fair or carnival', 0),
('arboretum', 'Visit an arboretum', 0),
('gardening', 'Plant a garden', 0),
('flowers', 'Pick some wildflowers', 0),
('coffee', 'Go to a coffee shop', 0),
('class', 'Take an art class', 0),
('art', 'Visit an art museum', 0),
('history', 'Visit a history museum', 0),
('campfire', 'Have a campfire', 0),
('barbecue', 'Host a barbecue', 0),
('tent', 'Sleep under the stars', 0),
('bubbles', 'Blow bubbles', 0),
('laser tag', 'Play laser tag', 0),
('playground', 'Take your kids to a playground', 0),
('water balloons', 'Have a water ballon battle', 0),
('concert', 'Take in an outdoor concert', 0),
('croquet', 'Play croquet', 0),
('bocce', 'Play bocce', 0),
('horseshoes', 'Play horseshoes', 0),
('darts', 'Play darts', 0),
('billiards', 'Play a game of billiards', 0),
('ice skating outside', 'Go ice skating at an outdoor rink', 0),
('skiing cross country', 'Go cross-country skiing', 0),
('star', 'Go stargazing', 0);

INSERT INTO pastime_alias
(name, pastime_id) VALUES
('jogging', 1),
('drawing', 12),
('dogs', 15),
('cats', 15),
('baseball', 27),
('canoeing', 41),
('cafe', 47),
('bonfire', 51);

-- insert action ballast row for all pastimes
insert into action (pastime_id, method_id)
select p._id, m._id
from pastime p
join selection_method m on m.name = 'ballast'
order by p._id

-- group
insert into measurement
(stat_id, action_id, value_integer)
select s._id, a._id, 1
from pastime p
join action a on a.pastime_id = p._id
join selection_method m on a.method_id = m._id and m.name = 'ballast'
join statistic s on s.name = 'group'
where p._id in (19,20,21,24,51,52,55,57,59,60,61)

-- not group
insert into measurement
(stat_id, action_id, value_integer)
select s._id, a._id, 0
from pastime p
join action a on a.pastime_id = p._id
join selection_method m on a.method_id = m._id and m.name = 'ballast'
join statistic s on s.name = 'group'
where p._id in (select a.pastime_id
from action a
join statistic s on s.name = 'group'
join selection_method sm on a.method_id = sm._id and sm.name = 'ballast'
left outer join measurement m on s._id = m.stat_id and a._id = m.action_id
where m._id is null)

-- not single
insert into measurement
(stat_id, action_id, value_integer)
select s._id, a._id, 0
from pastime p
join action a on a.pastime_id = p._id
join selection_method m on a.method_id = m._id and m.name = 'ballast'
join statistic s on s.name = 'single'
where p._id in (7,22,23,24,51,52,59,60,61,62,63)

-- single
insert into measurement
(stat_id, action_id, value_integer)
select s._id, a._id, 1
from pastime p
join action a on a.pastime_id = p._id
join selection_method m on a.method_id = m._id and m.name = 'ballast'
join statistic s on s.name = 'single'
where p._id in (select a.pastime_id
from action a
join statistic s on s.name = 'single'
join selection_method sm on a.method_id = sm._id and sm.name = 'ballast'
left outer join measurement m on s._id = m.stat_id and a._id = m.action_id
where m._id is null)

-- high temperature
insert into measurement
(stat_id, action_id, value_integer)
select s._id, a._id, 70
from action a
join pastime p on p._id = a.pastime_id
join statistic s on s.name = 'temperature'
where p.name in ('running', 'walking', 'tennis')

-- low temperature
insert into measurement
(stat_id, action_id, value_integer)
select s._id, a._id, 25
from action a
join pastime p on p._id = a.pastime_id
join statistic s on s.name = 'temperature'
where p.name in ('running', 'walking', 'tennis')

insert into measurement
(stat_id, action_id, value_integer)
select s._id, a._id, 70
from action a
join pastime p on p._id = a.pastime_id
join statistic s on s.name = 'temperature'
where p.name = 'walking'
