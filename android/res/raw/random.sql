select p._id as _id, p.name as name, p.action_name as action
from pastime p
join selection_method sm on sm.name = 'ballast'
join statistic sc on sc.name = 'crowd'
join statistic st on st.name = 'temperature'
join statistic sw on sw.name = 'weather'
-- only active pastimes
where p.active = 1
-- time
and p._id in (select pastime_id
from action
where method_id = sm._id
and datetime('now', 'start of day', time(performed))
between datetime('now', '-3 hours')
and datetime('now', '+3 hours', '-1 seconds'))
-- crowd
and p._id in (select a.pastime_id
from action a
join measurement m on m.action_id = a._id and m.stat_id = sc._id
where a.method_id = sm._id and m.value_text = ?)
-- temperature
and p._id in (select a.pastime_id
from action a
join measurement m on m.action_id = a._id and m.stat_id = st._id
where a.method_id = sm._id
and m.value_integer between ? - 10 and ? + 10)
-- weather
and p._id in (select a.pastime_id
from action a
join measurement m on m.action_id = a._id and m.stat_id = sw._id
where a.method_id = sm._id and m.value_text = ?)
-- order, limit
order by random() limit ?
