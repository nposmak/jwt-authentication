INSERT INTO roles (role_name) values ('adm');
INSERT INTO roles (role_name) values ('usr');

INSERT INTO users(email, password) values ('nposmak@ya.ru',
		 '$2a$12$8rEZ3WvMHdHorxtKR0v9zeY.eh1BtPxoRu/WF9mIPvSMhFPjIxocm');
		 
INSERT INTO users(email, password) values ('dposmak@ya.ru',
		 '$2a$12$qdEFX4i4Q5JLJzbQ4QDacOOMagMjVL/B.wOTbc4h97e.j.wF/ocX6');		 

INSERT INTO user_role (user_id, role_id) values (1, 1);
INSERT INTO user_role (user_id, role_id) values (2, 2);