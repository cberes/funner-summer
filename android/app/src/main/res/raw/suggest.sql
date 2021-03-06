select p._id as _id, p.name as name, p.action_name as action,
ifnull(nullif(t.p, 0.0), 0.001)
* ifnull(nullif(d.p, 0.0), 0.001)
* ifnull(nullif(g.p, 0.0), 0.001)
* ifnull(nullif(w.p, 0.0), 0.001)
* ifnull(nullif(temp.p, 0.0), 0.001) as p
from pastime p
-- compute percentage for stat: time
left outer join (select b._id, b.action_count, b.action_count * 1.0 / c.total as p
from (select a.pastime_id as _id, count(*) as action_count
from action a
where datetime('now', 'start of day', time(performed))
between datetime('now', '-3 hours') and datetime('now', '+3 hours', '-1 seconds')
group by a.pastime_id
) b, (select nullif(count(*), 0) as total
from action a
where datetime('now', 'start of day', time(performed))
between datetime('now', '-3 hours') and datetime('now', '+3 hours', '-1 seconds')
) c) t on t._id = p._id
-- compute percentage for stat: day
left outer join (select b._id, b.action_count, b.action_count * 1.0 / c.total as p
from (select a.pastime_id as _id, count(*) as action_count
from action a
join selection_method sm on a.method_id = sm._id and sm.name <> 'ballast'
where a.day = CAST (strftime('%w', date('now')) AS INTEGER)
group by a.pastime_id
) b, (select nullif(count(*), 0) as total
from action a
join selection_method sm on a.method_id = sm._id and sm.name <> 'ballast'
where a.day = CAST (strftime('%w', date('now')) AS INTEGER)
) c) d on d._id = p._id
-- compute percentage for stat: crowd
left outer join (select b._id, b.action_count, b.action_count * 1.0 / c.total as p
from (select a.pastime_id as _id, count(*) as action_count from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'crowd'
and m.value_text = ?
group by a.pastime_id
) b, (select nullif(count(*), 0) as total from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'crowd'
and m.value_text = ?
) c) g on g._id = p._id
-- compute percentage for stat: weather
left outer join (select b._id, b.action_count, b.action_count * 1.0 / c.total as p
from (select a.pastime_id as _id, count(*) as action_count from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'weather' and m.value_text = ?
group by a.pastime_id
) b, (select nullif(count(*), 0) as total from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'weather' and m.value_text = ?
) c) w on w._id = p._id
-- compute percentage for stat: temperature
left outer join (select b._id, b.action_count, b.action_count * 1.0 / c.total as p
from (select a.pastime_id as _id, count(*) as action_count from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'temperature'
and m.value_integer between ? - 10 and ? + 9.999
group by a.pastime_id
) b, (select nullif(count(*), 0) as total from measurement m
join statistic s on s._id = m.stat_id
join action a on a._id = m.action_id
where s.name = 'temperature'
and m.value_integer between ? - 10 and ? + 9.999
) c) temp on temp._id = p._id
-- only active pastimes
where p.active = 1
-- order by total probability
order by 4 desc, random()
limit ?
