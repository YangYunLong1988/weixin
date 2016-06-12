-- Create sequence 
create sequence HIBERNATE_SEQUENCE
minvalue 1000
maxvalue 9999999999999999999999999999
start with 1000
increment by 1
cache 20;

insert into DIANA_USER (DTYPE, ID, CREATED_DATE, LAST_MODIFIED_DATE, EMAIL, MOBILE, PASSWORD, PLATFORM, ROLE, STATUS, USERNAME, CREATED_BY_ID, LAST_MODIFIED_BY_ID)
values ('Customer', 1, null, null, null, null, null, null, '系统', null, null, null, null);