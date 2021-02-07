select setval('dasit.groups_id_seq', 100);
select setval('dasit.datasets_id_seq', 100);

insert into dasit.groups (id, name, email, status)
values
(1, 'testgroup1', 'testgroup@email.com', 'ACTIVE');

insert into dasit.datasets (id, name, owner_group, status)
values
(1, 'testset1', 1, 'ACTIVE');

insert into dasit.datasets_published (dataset_id, publish_start_dt, publish_end_dt)
values
(1, '2020-08-01', '2020-08-12'),
(1, '2020-08-14', '2020-08-22'),
(1, '2020-08-23', '2020-08-25');
