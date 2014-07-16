select p._id as _id, p.name as name, p.action_name as action,
t.p as time_p, d.p as day_p, g.p as group_p,
sng.p as single_p, w.p as weather_p, temp.p as temp_p,
ifnull(nullif(t.p, 0.0), 0.001)
* ifnull(nullif(d.p, 0.0), 0.001)
* ifnull(nullif(g.p, 0.0), 0.001)
* ifnull(nullif(sng.p, 0.0), 0.001)
* ifnull(nullif(w.p, 0.0), 0.001)
* ifnull(nullif(temp.p, 0.0), 0.001) as p
from pastime p
-- compute percentage for stat: time
left outer join (select b._id, b.action_count, b.action_count * 1.0 / c.total as p
from (select a.pastime_id as _id, count(*) as action_count
from action a
where datetime('now', 'start of day', time(performed)) > datetime('now', '-3 hours')
and datetime('now', 'start of day', time(performed)) < datetime('now', '+3 hours')
group by a.pastime_id
) b, (select count(*) as total
from action a
where datetime('now', 'start of day', time(performed)) > datetime('now', '-3 hours')
and datetime('now', 'start of day', time(performed)) < datetime('now', '+3 hours')
) c) t on t._id = p._id
-- compute percentage for stat: day
left outer join (select b._id, b.action_count, b.action_count * 1.0 / c.total as p
from (select a.pastime_id as _id, count(*) as action_count
from action a
where a.day = CAST (strftime('%w', date('now')) AS INTEGER)
group by a.pastime_id
) b, (select count(*) as total
from action a
where a.day = CAST (strftime('%w', date('now')) AS INTEGER)
) c) d on d._id = p._id
-- compute percentage for stat: group
left outer join (select b._id, b.action_count, b.action_count * 1.0 / c.total as p
from (select a.pastime_id as _id, count(*) as action_count from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'group'
and m.value_integer = ?
group by a.pastime_id
) b, (select count(*) as total from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'group'
and m.value_integer = ?
) c) g on g._id = p._id
-- compute percentage for stat: single
left outer join (select b._id, b.action_count, b.action_count * 1.0 / c.total as p
from (select a.pastime_id as _id, count(*) as action_count from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'single'
and m.value_integer = ?
group by a.pastime_id
) b, (select count(*) as total from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'single'
and m.value_integer = ?
) c) sng on sng._id = p._id
-- compute percentage for stat: weather
left outer join (select b._id, b.action_count, b.action_count * 1.0 / c.total as p
from (select a.pastime_id as _id, count(*) as action_count from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'weather'
and (m.value_text like '%' || ? || '%' or ? like '%' || m.value_text || '%')
group by a.pastime_id
) b, (select count(*) as total from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'weather'
and (m.value_text like '%' || ? || '%' or ? like '%' || m.value_text || '%')
) c) w on w._id = p._id
-- compute percentage for stat: temperature
left outer join (select b._id, b.action_count, b.action_count * 1.0 / c.total as p
from (select a.pastime_id as _id, count(*) as action_count from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'temperature'
and m.value_integer between ? - 10 and ? + 10
group by a.pastime_id
) b, (select count(*) as total from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'temperature'
and m.value_integer between ? - 10 and ? + 10
) c) temp on temp._id = p._id
-- only active pastimes
WHERE p.active = 1
-- order by total probability (prb)
ORDER BY ifnull(nullif(t.p, 0.0), 0.001)
* ifnull(nullif(d.p, 0.0), 0.001)
* ifnull(nullif(g.p, 0.0), 0.001)
* ifnull(nullif(sng.p, 0.0), 0.001)
* ifnull(nullif(w.p, 0.0), 0.001)
* ifnull(nullif(temp.p, 0.0), 0.001) DESC
LIMIT ?
