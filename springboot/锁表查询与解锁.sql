--查询锁表情况
select 
    l.oracle_username, 
    l.os_user_name, 
    l.locked_mode,  
    o.object_id, 
    o.object_name, 
    o.object_type,
    s.sid, 
    s.serial#, 
    s.machine,
    s.terminal, 
    s.port,
    p.pid, 
    p.spid  
from v$locked_object l
left join dba_objects o on l.object_id = o.object_id
left join v$session s on l.session_id = s.sid
left join v$process p on l.process = p.pid
order by p.spid;
--查询锁表语句
select s.sid, 
       s.serial#, 
       s.user#,
       s.machine,
       s.terminal, 
       l.locked_mode, 
       l.oracle_username,
       l.os_user_name,
       a.sql_text, 
       a.action 
from gv$locked_object l
left join gv$session s on l.session_id = s.sid
left join gv$sqlarea a on s.prev_sql_addr = a.address 
order by s.sid, s.serial#;
--解锁
alter system kill session '146';--146为锁住的进程号，即spid
alter system kill session '79, 3109';-- sid和serial#组合
--v$与gv$区别:https://blog.csdn.net/vic_qxz/article/details/52748019
select * from v$locked_object;
select * from gv$locked_object --多了个inst_id列