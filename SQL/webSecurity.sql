create table users(
username varchar2(50) not null primary key,
password varchar2(50) not null,
enabled char(1) default '1');

create table authorities(
username varchar2(50) not null,
authority varchar2(50) not null,
CONSTRAINT fk_authorities_users foreign key(username) REFERENCES users(username));

-- unique�ε��� ���� (�ߺ��Ȱ��� ������ ����x �̹������ �� ������ �߰� x)
create UNIQUE index ix_auth_username on authorities(username,authority);

insert into users(username,password) values('user00','pw00');
insert into users(username,password) values('member00','member00');
insert into users(username,password) values('admin00','admin00');

insert into authorities (username, authority) values('user00','ROLE_USER');
insert into authorities (username, authority) values('member00','ROLE_MANAGER');
insert into authorities (username, authority) values('admin00','ROLE_MANAGER');
insert into authorities (username, authority) values('admin00','ROLE_ADMIN');
commit;

-----------------------------------------------
create table tbl_member(
userid varchar2(50) not null PRIMARY key,
userpw varchar2(100) not null,
username varchar2(100) not null,
regdate date default sysdate,
updatedate date default sysdate,
enabled char(1) default '1');

create table tbl_member_auth(
userid varchar2(50) not null,
auth varchar2(50) not null,
CONSTRAINT fk_member_auth FOREIGN key(userid) REFERENCES tbl_member(userid)
);

commit;
--�α��� ����
select userid, username, userpw password, enabled
from tbl_member
where userid = 'admin90';
--����
select userid username, auth authority
from tbl_member_auth
where userid = 'admin90';