select setval('dasit.groups_id_seq', 100);
select setval('dasit.datasets_id_seq', 100);

insert into dasit.groups (id, name, email, status)
values
(1, 'testgroup1', 'testgroup@email.com', 'ACTIVE'),
(2, 'testgroup2', 'testgroup@email.com', 'ACTIVE'),
(3, 'testgroup3', 'deprecatedgroup@email.com', 'ACTIVE'),
(4, 'testgroup4', 'inactivegroup@email.com', 'INACTIVE'),
(5, 'standAloneGroup1', 'another@email.com', 'ACTIVE'),
(6, 'testgroup6', 'testgroup6@email.com', 'ACTIVE'),
(7, 'testgroup7', 'testgroup7@email.com', 'ACTIVE')
;

insert into dasit.datasets (id, name, owner_group, status)
values
(1, 'testset1', 1, 'ACTIVE'),
(2, 'testset2', 1, 'INACTIVE'),
(3, 'testset3', 2, 'ACTIVE'),
(4, 'deprecated1', 2, 'ACTIVE'),
(5, 'todeprecate1', 6, 'ACTIVE')
;

insert into dasit.datasets_published (dataset_id, publish_start_dt, publish_end_dt)
values
(1, '2020-08-01', '2020-08-12'),
(1, '2020-08-14', '2020-08-22'),
(1, '2020-08-23', '2020-08-25'),
(3, '2020-01-15', '2020-01-20'),
(3, '2020-01-25', '2020-01-31'),
(3, '2020-11-13', '2020-11-15'),
(3, '2020-11-16', '2020-11-17'),
(3, '2020-11-18', '2020-11-20'),
(3, '2020-08-11', '2020-08-22')
;
