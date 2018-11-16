--��ѯ�������
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
--��ѯ�������
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
--����
alter system kill session '146';--146Ϊ��ס�Ľ��̺ţ���spid
alter system kill session '79, 3109';-- sid��serial#���
--v$��gv$����:https://blog.csdn.net/vic_qxz/article/details/52748019
select * from v$locked_object;
select * from gv$locked_object --���˸�inst_id��