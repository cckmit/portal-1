insert into case_state(state, info) values ('nx.request', 'request to NX');
insert into case_state(state, info) values ('customer.request', 'request to customer');

insert into case_state_matrix(case_type, case_state, view_order, info) values (4, 35, 4, 'request to NX');
insert into case_state_matrix(case_type, case_state, view_order, info) values (4, 36, 4, 'request to customer');

insert into case_state_workflow_link(workflow_id, state_from, state_to) values (1, 2, 35);
insert into case_state_workflow_link(workflow_id, state_from, state_to) values (1, 2, 36);
insert into case_state_workflow_link(workflow_id, state_from, state_to) values (1, 35, 2);
insert into case_state_workflow_link(workflow_id, state_from, state_to) values (1, 36, 2);