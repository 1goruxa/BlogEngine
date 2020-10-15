insert into tags (id, text) values ('1','STRING');
insert into tags (id, text) values ('2','SOME TAG');
insert into tags (id, text) values ('3','SOME OTHER TAG');
insert into tag2post (id, post_id, tagid) values ('1','1','1');
insert into tag2post (id, post_id, tagid) values ('2','1','2');
insert into tag2post (id, post_id, tagid) values ('3','2','3');
insert into post_votes(id, post_id, time, user_id, value) values ('1', '1', '2020-01-01 00:00:01', '1', '1');
insert into post_votes(id, post_id, time, user_id, value) values ('2', '1', '2020-01-01 00:00:01', '1', '1');
insert into post_votes(id, post_id, time, user_id, value) values ('3', '2', '2020-01-01 00:00:01', '1', '-1');
insert into post_votes(id, post_id, time, user_id, value) values ('4', '1', '2020-01-01 00:00:01', '1', '-1');

insert into post_comments(id,parent_id,post_id,text,time,user_id) values ('1','0','1','test comment','2020-01-01 00:00:03','1');
insert into post_comments(id,parent_id,post_id,text,time,user_id) values ('2','0','2','test comment2','2020-01-01 00:00:03','1');
insert into post_comments(id,parent_id,post_id,text,time,user_id) values ('3','1','1','test comment with parent','2020-01-01 00:00:03','1');

