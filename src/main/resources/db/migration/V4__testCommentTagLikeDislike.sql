insert into tags (id, text) values ('1','STRING');
insert into tag2post (id, post_id, tagid) values ('1','1','1');
insert into post_votes(id, post_id, time, user_id, value) values ('1', '1', '2020-01-01 00:00:01', '1', '3');
insert into post_comments(id,parent_id,post_id,text,time,user_id) values ('1','0','1','test comment','2020-01-01 00:00:03','1');
