create table if not exists audit
(
    id                bigint auto_increment
        primary key,
    type              int          not null,
    created           datetime     not null,
    creator           bigint       not null,
    entry_info        mediumtext   not null,
    creator_shortname varchar(128) null,
    creator_ip        varchar(32)  null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists case_state
(
    ID                 int auto_increment
        primary key,
    STATE              varchar(32)   not null,
    INFO               varchar(256)  null,
    usage_in_companies int default 1 null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index UQ_CASE_STATE
    on case_state (STATE);

create table if not exists case_state_matrix
(
    ID         int auto_increment
        primary key,
    CASE_TYPE  int          not null,
    CASE_STATE int          not null,
    view_order int          null,
    OLD_ID     int          null,
    OLD_CODE   varchar(32)  null,
    info       varchar(500) null,
    constraint FK_CSMATRIX_STATE
        foreign key (CASE_STATE) references case_state (ID)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index uq_cstate_matrix
    on case_state_matrix (CASE_TYPE, CASE_STATE);

create table if not exists case_state_workflow
(
    id   bigint      not null
        primary key,
    info varchar(64) null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists case_state_workflow_link
(
    id          bigint auto_increment
        primary key,
    workflow_id bigint not null,
    state_from  int    not null,
    state_to    int    not null,
    constraint fk_state_from_to_case_state
        foreign key (state_from) references case_state (ID),
    constraint fk_state_to_to_case_state
        foreign key (state_to) references case_state (ID),
    constraint fk_workflow_id_to_case_state_workflow
        foreign key (workflow_id) references case_state_workflow (id)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists case_type
(
    ID      int          not null
        primary key,
    CT_CODE varchar(16)  not null,
    CT_INFO varchar(128) null,
    NEXT_ID bigint       null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index UQ_CASE_TYPE
    on case_type (CT_CODE);

create table if not exists company_group
(
    id         bigint auto_increment
        primary key,
    created    datetime default CURRENT_TIMESTAMP null,
    group_name varchar(200)                       null,
    group_info varchar(1000)                      null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists company
(
    id                bigint auto_increment
        primary key,
    created           datetime         null,
    cname             varchar(512)     not null,
    info              varchar(4000)    null,
    category_id       bigint           null,
    contactInfo       json             null,
    groupId           bigint           null,
    old_id            bigint           null,
    parent_company_id bigint           null,
    is_hidden         bit              null,
    is_deprecated     bit default b'0' not null,
    constraint FK_PARENT_COMPANY
        foreign key (parent_company_id) references company (id)
            on delete set null,
    constraint fk_compgroup_smp
        foreign key (groupId) references company_group (id)
            on delete set null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists case_state_to_company
(
    id         bigint auto_increment
        primary key,
    state_id   int    null,
    company_id bigint null,
    constraint FK_CASE_STATE_TO_COMPANY_CASE_STATE_ID
        foreign key (state_id) references case_state (ID)
            on delete cascade,
    constraint FK_CASE_STATE_TO_COMPANY_COMPANY_ID
        foreign key (company_id) references company (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index ix_company_group
    on company (groupId);

create index ix_company_old_id
    on company (old_id);

create index uq_company
    on company (cname);

create table if not exists company_dep
(
    id         bigint auto_increment
        primary key,
    company_id bigint        not null,
    created    datetime      not null,
    dep_name   varchar(200)  not null,
    dep_info   varchar(1000) null,
    parent_dep bigint        null,
    head_id    bigint        null,
    dep_extId  varchar(32)   not null,
    constraint FK_CD_PARENT
        foreign key (parent_dep) references company_dep (id),
    constraint FK_COMPANY_DEP
        foreign key (company_id) references company (id)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index uq_dep_name
    on company_dep (company_id, dep_name);

create index uq_ext_dep
    on company_dep (company_id, dep_extId);

create index uq_compgroup
    on company_group (group_name);

create table if not exists company_group_home
(
    id            bigint auto_increment
        primary key,
    companyId     bigint      not null,
    external_code varchar(32) null,
    mainId        bigint      null,
    constraint ix_external_code
        unique (external_code),
    constraint FK_CGH_MAIN
        foreign key (mainId) references company (id)
            on delete cascade,
    constraint FK_COMP_HOME_GROUP
        foreign key (companyId) references company (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists company_group_item
(
    id         bigint auto_increment
        primary key,
    group_id   bigint not null,
    company_id bigint not null,
    constraint UQ_COMP_GR_ITEM
        unique (group_id, company_id),
    constraint FK_CG_COMPANY
        foreign key (company_id) references company (id),
    constraint FK_CG_GROUP
        foreign key (group_id) references company_group (id)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists databasechangelog
(
    ID            varchar(255) not null,
    AUTHOR        varchar(255) not null,
    FILENAME      varchar(255) not null,
    DATEEXECUTED  datetime     not null,
    ORDEREXECUTED int          not null,
    EXECTYPE      varchar(10)  not null,
    MD5SUM        varchar(35)  null,
    DESCRIPTION   varchar(255) null,
    COMMENTS      varchar(255) null,
    TAG           varchar(255) null,
    LIQUIBASE     varchar(20)  null,
    CONTEXTS      varchar(255) null,
    LABELS        varchar(255) null,
    DEPLOYMENT_ID varchar(10)  null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists databasechangeloglock
(
    ID          int          not null
        primary key,
    LOCKED      bit          not null,
    LOCKGRANTED datetime     null,
    LOCKEDBY    varchar(255) null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists dev_unit
(
    ID              bigint auto_increment
        primary key,
    UTYPE_ID        int          not null,
    CREATED         datetime     not null,
    UNIT_NAME       varchar(256) not null,
    UNIT_INFO       text         null,
    LAST_UPDATE     datetime     null,
    CREATOR_ID      bigint       null,
    UNIT_STATE      int          null,
    old_id          bigint       null,
    wiki_link       varchar(256) null,
    history_version mediumtext   null,
    cdr_description mediumtext   null,
    configuration   mediumtext   null,
    aliases         varchar(500) null
)
    comment 'Элементы разработки :
- компоненты (бывшие проекты .CP' ENGINE = INNODB
                                  CHARACTER SET UTF8
                                  COLLATE utf8_general_ci;

create index FK_DEVUNIT_STATE
    on dev_unit (UNIT_STATE);

create index FK_DEV_UNIT_TYPE
    on dev_unit (UTYPE_ID);

create index UQ_PROJECT_NAME
    on dev_unit (UNIT_NAME);

create index ix_devcomp_creator
    on dev_unit (CREATOR_ID);

create index ix_devunit_old_id
    on dev_unit (old_id, UTYPE_ID);

create table if not exists dev_unit_children
(
    ID       bigint auto_increment
        primary key,
    DUNIT_ID bigint not null,
    CHILD_ID bigint not null,
    constraint FK_DEVUNIT_CHILD
        foreign key (CHILD_ID) references dev_unit (ID),
    constraint FK_DEVUNIT_CONT
        foreign key (DUNIT_ID) references dev_unit (ID)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index ix_devunit_child
    on dev_unit_children (CHILD_ID);

create index ix_devunit_cont
    on dev_unit_children (DUNIT_ID);

create table if not exists dev_unit_mrole
(
    ID         int auto_increment
        primary key,
    UROLE_CODE varchar(32)   not null,
    UROLE_INFO varchar(2000) null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index UQ_DUNIT_MROLE
    on dev_unit_mrole (UROLE_CODE);

create table if not exists dev_unit_subscription
(
    id          bigint auto_increment
        primary key,
    dev_unit_id bigint      not null,
    email_addr  varchar(64) not null,
    lang_code   varchar(16) null,
    constraint uq_company_subscription
        unique (email_addr, dev_unit_id),
    constraint fk_devunit_subscription
        foreign key (dev_unit_id) references dev_unit (ID)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists document_type
(
    id                bigint auto_increment
        primary key,
    name              varchar(128) not null,
    document_category varchar(32)  null,
    short_name        varchar(16)  null,
    gost              varchar(64)  null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists export_syb_entry
(
    id          bigint auto_increment
        primary key,
    CREATED     datetime default CURRENT_TIMESTAMP not null,
    instance_id varchar(32)                        not null,
    local_id    bigint                             not null,
    obj_type    varchar(32)                        not null,
    obj_dump    mediumtext                         not null,
    constraint uq_export_syb_entry
        unique (instance_id)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index ix_export_syb_entry
    on export_syb_entry (instance_id);

create table if not exists importance_level
(
    id   int auto_increment
        primary key,
    code varchar(32)   not null,
    info varchar(1000) null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index UQ_IMPOTANCE_LEVEL
    on importance_level (code);

create table if not exists jira_company_group
(
    id                bigint auto_increment
        primary key,
    jira_company_name varchar(200) null,
    company_id        bigint       null,
    constraint fk_company_id
        foreign key (company_id) references company (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists jira_priority_map
(
    id   bigint auto_increment
        primary key,
    name varchar(128) null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists jira_priority_map_entry
(
    id                  bigint auto_increment
        primary key,
    MAP_ID              bigint       null,
    Jira_priority_id    bigint       null,
    LOCAL_priority_id   bigint       null,
    LOCAL_priority_name varchar(128) null,
    jira_priority_name  varchar(128) null,
    jira_sla_info       varchar(128) null,
    constraint FK_JIRA_EP_PRIORITY_ENTRY
        foreign key (MAP_ID) references jira_priority_map (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists jira_sla_map
(
    id   bigint auto_increment
        primary key,
    name varchar(128) null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists jira_sla_map_entry
(
    id                   bigint auto_increment
        primary key,
    MAP_ID               bigint        null,
    issue_type           varchar(256)  not null,
    severity             varchar(256)  null,
    description          varchar(1024) null,
    time_of_reaction_min bigint        null,
    time_of_decision_min bigint        not null,
    constraint FK_JIRA_EP_SLA_ENTRY
        foreign key (MAP_ID) references jira_sla_map (id)
            on update cascade on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists jira_status_map
(
    id   bigint auto_increment
        primary key,
    name varchar(128) null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists jira_endpoint
(
    id              bigint auto_increment
        primary key,
    server_addr     varchar(64)  not null,
    project_id      varchar(128) not null,
    COMPANY_ID      bigint       not null,
    STATUS_MAP_ID   bigint       null,
    PRIORITY_MAP_ID bigint       null,
    person_id       bigint       null,
    server_login    varchar(32)  null,
    server_pwd      varchar(32)  null,
    SLA_MAP_ID      bigint       null,
    constraint FK_JIRA_EP_COMPANY
        foreign key (COMPANY_ID) references company (id)
            on delete cascade,
    constraint FK_JIRA_EP_PRIORITY
        foreign key (PRIORITY_MAP_ID) references jira_priority_map (id)
            on delete cascade,
    constraint FK_JIRA_EP_SLA
        foreign key (SLA_MAP_ID) references jira_sla_map (id)
            on update cascade on delete cascade,
    constraint FK_JIRA_EP_STATUS
        foreign key (STATUS_MAP_ID) references jira_status_map (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists jira_status_map_entry
(
    id                bigint auto_increment
        primary key,
    MAP_ID            bigint       null,
    Jira_status_name  varchar(256) null,
    LOCAL_status_id   bigint       null,
    LOCAL_status_name varchar(256) null,
    constraint FK_JIRA_EP_STATUS_ENTRY
        foreign key (MAP_ID) references jira_status_map (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists location
(
    ID          bigint auto_increment
        primary key,
    PARENT_ID   bigint        null,
    TYPE_ID     int           null,
    NAME        varchar(256)  not null,
    DESCRIPTION varchar(1024) null,
    OLD_ID      bigint        null,
    CODE        varchar(32)   null,
    PATH        varchar(256)  not null,
    constraint FK_LOCATION_PARENT
        foreign key (PARENT_ID) references location (ID)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index FK_LOCATION_TYPE
    on location (TYPE_ID);

create table if not exists migrationentry
(
    id         int auto_increment
        primary key,
    entry_code varchar(64) not null,
    lastUpdate datetime    null,
    last_id    bigint      not null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index uq_migration_entry
    on migrationentry (entry_code);

create table if not exists person
(
    id               bigint auto_increment
        primary key,
    created          datetime                not null,
    creator          varchar(256)            not null,
    company_id       bigint                  not null,
    firstname        varchar(128)            null,
    lastname         varchar(128)            null,
    secondname       varchar(128)            null,
    displayname      varchar(256)            not null,
    displayPosition  varchar(500)            null,
    sex              char                    not null,
    birthday         datetime                null,
    passportinfo     varchar(2000)           null,
    info             varchar(2000)           null,
    ipaddress        varchar(64)             null,
    isdeleted        int        default 0    not null,
    department_id    bigint                  null,
    department       varchar(256)            null,
    displayShortName varchar(128)            null,
    isfired          int        default 0    not null,
    contactInfo      json                    null,
    relations        varchar(128)            null,
    old_id           bigint                  null,
    locale           varchar(8) default 'ru' null,
    firedate         datetime                null,
    constraint FK_PERSON_COMPANY
        foreign key (company_id) references company (id)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists attachment
(
    ID            bigint auto_increment
        primary key,
    CREATED       datetime      not null,
    CREATOR       bigint        not null,
    AT_LABEL      varchar(1000) null,
    EXT_LINK      varchar(1000) null,
    DATA_MIMETYPE varchar(128)  null,
    DATA_SIZE     bigint        null,
    FILE_NAME     varchar(1000) null,
    constraint FK_ATTACH_CREATOR
        foreign key (CREATOR) references person (id)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index ix_att_creator
    on attachment (CREATOR);

create table if not exists case_object
(
    ID                         bigint auto_increment
        primary key,
    CASE_TYPE                  int                  not null,
    CASENO                     bigint               not null,
    CREATED                    datetime             not null,
    MODIFIED                   datetime             null,
    CASE_NAME                  varchar(1024)        not null,
    EXT_ID                     varchar(32)          null,
    info                       mediumtext           null,
    STATE                      int                  not null,
    IMPORTANCE                 int                  null,
    initiator                  bigint               null,
    CREATOR                    bigint               null,
    CREATOR_IP                 varchar(32)          null,
    MANAGER                    bigint               null,
    KEYWORDS                   varchar(500)         null,
    ISLOCAL                    int        default 0 not null,
    EMAILS                     varchar(2000)        null,
    creator_info               varchar(200)         null,
    initiator_company          bigint               null,
    product_id                 bigint               null,
    deleted                    int        default 0 not null,
    private_flag               int        default 0 not null,
    ATTACHMENT_EXISTS          tinyint(1) default 0 not null,
    EXT_APP                    varchar(16)          null,
    EXT_APP_ID                 varchar(64)          null,
    EXT_APP_DATA               mediumtext           null,
    time_elapsed               bigint     default 0 null,
    email_last_id              bigint     default 0 null,
    platform_id                bigint               null,
    technical_support_validity datetime             null,
    constraint EXT_APP_ID
        unique (EXT_APP_ID),
    constraint UQ_CASE
        unique (CASENO, CASE_TYPE),
    constraint FK_CASE_CREATOR
        foreign key (CREATOR) references person (id),
    constraint FK_CASE_IMPORTANCE
        foreign key (IMPORTANCE) references importance_level (id),
    constraint FK_CASE_INITIATOR
        foreign key (initiator) references person (id),
    constraint FK_CASE_MANAGER
        foreign key (MANAGER) references person (id),
    constraint FK_CASE_STATE
        foreign key (STATE) references case_state (ID),
    constraint fk_case_product
        foreign key (product_id) references dev_unit (ID),
    constraint fk_caseobj_initcomp
        foreign key (initiator_company) references company (id)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists case_link
(
    id        bigint auto_increment
        primary key,
    case_id   bigint      not null,
    link_type varchar(16) not null,
    remote_id text        not null,
    constraint case_link_case_id_fk
        foreign key (case_id) references case_object (ID)
            on update cascade on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists case_comment
(
    ID                        bigint auto_increment
        primary key,
    CREATED                   datetime default CURRENT_TIMESTAMP not null,
    CLIENT_IP                 varchar(32)                        null,
    CASE_ID                   bigint                             not null,
    AUTHOR_ID                 bigint                             not null,
    CSTATE_ID                 int                                null,
    REPLY_TO                  bigint                             null,
    COMMENT_TEXT              mediumtext                         null,
    OLD_ID                    bigint                             null,
    time_elapsed              bigint                             null,
    cimp_level                int                                null,
    remote_id                 varchar(64)                        null,
    remote_link_id            bigint                             null,
    original_author_name      varchar(64)                        null,
    original_author_full_name varchar(256)                       null,
    time_elapsed_type         int                                null,
    private_flag              bit      default b'0'              not null,
    cmanager_id               bigint                             null,
    constraint FK_CASECOMMENT_STATE
        foreign key (CSTATE_ID) references case_state (ID),
    constraint FK_CASE_COMMENT
        foreign key (CASE_ID) references case_object (ID),
    constraint FK_CSCOMM_AUTHOR
        foreign key (AUTHOR_ID) references person (id),
    constraint FK_CSCOMM_REPLY
        foreign key (REPLY_TO) references case_comment (ID)
            on delete cascade,
    constraint case_comment_remote_link_id_fk
        foreign key (remote_link_id) references case_link (id),
    constraint cmanager_id_to_person_fk
        foreign key (cmanager_id) references person (id)
            on update cascade,
    constraint fk_case_comment_importance
        foreign key (cimp_level) references importance_level (id)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists case_attachment
(
    ID          bigint auto_increment
        primary key,
    CASE_ID     bigint      not null,
    ATT_ID      bigint      not null,
    CCOMMENT_ID bigint      null,
    remote_id   varchar(64) null,
    constraint remote_id
        unique (remote_id),
    constraint FK_CASE_ATTACHMENT
        foreign key (CASE_ID) references case_object (ID)
            on delete cascade,
    constraint FK_CS_ATTACHMENT
        foreign key (ATT_ID) references attachment (ID),
    constraint FK_PARENT_COMMENT
        foreign key (CCOMMENT_ID) references case_comment (ID)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index IX_CSATT
    on case_attachment (CASE_ID, ATT_ID);

create index ix_casecomment
    on case_comment (CASE_ID);

create index ix_casecomment_ap
    on case_comment (AUTHOR_ID);

create table if not exists case_location
(
    ID          bigint auto_increment
        primary key,
    CASE_ID     bigint null,
    LOCATION_ID bigint null,
    constraint FK_CL_CASE
        foreign key (CASE_ID) references case_object (ID),
    constraint FK_CL_LOCATION
        foreign key (LOCATION_ID) references location (ID)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists case_member
(
    ID             bigint auto_increment
        primary key,
    CASE_ID        bigint null,
    MEMBER_ID      bigint null,
    MEMBER_ROLE_ID int    null,
    constraint FK_CO_CASE
        foreign key (CASE_ID) references case_object (ID),
    constraint FK_CO_MEMBER
        foreign key (MEMBER_ID) references person (id),
    constraint FK_CO_MEMBER_ROLE
        foreign key (MEMBER_ROLE_ID) references dev_unit_mrole (ID)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists case_notifier
(
    id        bigint auto_increment
        primary key,
    CASE_ID   bigint not null,
    PERSON_ID bigint not null,
    constraint FK_CI_REF
        foreign key (CASE_ID) references case_object (ID)
            on delete cascade,
    constraint FK_PI_REF
        foreign key (PERSON_ID) references person (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index ix_ci_ref
    on case_notifier (CASE_ID);

create index ix_pi_ref
    on case_notifier (PERSON_ID);

create index uq_cipi
    on case_notifier (CASE_ID, PERSON_ID);

create index FK_CASE_TYPE
    on case_object (CASE_TYPE);

create index UQ_CASE_EXTID
    on case_object (EXT_ID);

create index ix_case_creator
    on case_object (CREATOR);

create index ix_case_initiator
    on case_object (initiator);

create index ix_case_kwords
    on case_object (KEYWORDS);

create index ix_case_manager
    on case_object (MANAGER);

create index ix_casename
    on case_object (CASE_NAME);

create table if not exists case_tag
(
    id         bigint auto_increment
        primary key,
    case_type  int         not null,
    name       varchar(64) not null,
    color      varchar(9)  null,
    company_id bigint      not null,
    person_id  bigint      null,
    constraint case_tag_name_unique
        unique (case_type, company_id, name),
    constraint fk_company_case_tag
        foreign key (company_id) references company (id),
    constraint fk_person_case_tag
        foreign key (person_id) references person (id)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists case_object_tag
(
    id      bigint auto_increment
        primary key,
    case_id bigint not null,
    tag_id  bigint not null,
    constraint case_object_tag_to_case_fk
        foreign key (case_id) references case_object (ID)
            on update cascade on delete cascade,
    constraint case_object_tag_to_tag_fk
        foreign key (tag_id) references case_tag (id)
            on update cascade on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists contract
(
    id                 bigint   not null
        primary key,
    contract_type      int      not null,
    cost               bigint   null,
    date_signing       datetime null,
    date_valid         datetime null,
    cost_currency      int      null,
    organization_id    bigint   null,
    parent_contract_id bigint   null,
    project_id         bigint   null,
    constraint project_id
        unique (project_id),
    constraint `contract-case_object-id-fk`
        foreign key (id) references case_object (ID)
            on update cascade on delete cascade,
    constraint fk_organization_company
        foreign key (organization_id) references company (id),
    constraint fk_parent_contract
        foreign key (parent_contract_id) references contract (id)
            on update cascade on delete set null,
    constraint project_id_to_case_object_fk
        foreign key (project_id) references case_object (ID)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists contract_date
(
    id          bigint auto_increment
        primary key,
    contract_id bigint        not null,
    date        datetime      null,
    comment     varchar(2048) null,
    type        int           null,
    notify      bit           null,
    constraint fk_contract_id_to_contract
        foreign key (contract_id) references contract (id)
            on update cascade on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists employee_registration
(
    id                     bigint           not null
        primary key,
    employment_date        date             null,
    employment_type        int              null,
    with_registration      bit default b'1' not null,
    position               varchar(128)     null,
    workplace              text             null,
    equipment_list         varchar(64)      null,
    resource_list          varchar(128)     null,
    phone_office_type_list varchar(64)      null,
    probation_period       int              null,
    resource_comment       varchar(512)     null,
    operating_system       varchar(64)      null,
    additional_soft        varchar(512)     null,
    curators               varchar(64)      null,
    person                 bigint           null,
    constraint FK_PERSON_PERSON_ID
        foreign key (person) references person (id)
            on delete cascade,
    constraint `employee_registration-case_object-id-fk`
        foreign key (id) references case_object (ID)
            on update cascade on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists equipment
(
    id                  bigint auto_increment
        primary key,
    name                varchar(128)                          not null,
    name_sldwrks        varchar(128)                          null,
    created             datetime    default CURRENT_TIMESTAMP null,
    type                varchar(64) default 'DETAIL'          not null,
    linked_equipment_id bigint                                null,
    comment             varchar(1024)                         null,
    author_id           bigint                                not null,
    manager_id          bigint                                null,
    project_id          bigint                                null,
    constraint equipment_author_id_fk
        foreign key (author_id) references person (id),
    constraint equipment_manager_id_fk
        foreign key (manager_id) references person (id),
    constraint equipment_project_id_fk
        foreign key (project_id) references case_object (ID)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists decimal_number
(
    id                  bigint auto_increment
        primary key,
    org_code            varchar(4)    not null,
    classifier_code     int           not null,
    reg_number          int           not null,
    modification_number int           null,
    entity_id           bigint        null,
    is_reserve          int default 0 not null,
    constraint decimal_number_uk
        unique (org_code, classifier_code, reg_number, modification_number),
    constraint decimal_number_entity_id_fk
        foreign key (entity_id) references equipment (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index decimal_number_equipment_id_fk
    on decimal_number (entity_id);

create table if not exists document
(
    id               bigint auto_increment
        primary key,
    name             varchar(512)                       null,
    inventory_number bigint                             null,
    type_id          bigint                             not null,
    annotation       text                               null,
    project_id       bigint                             null,
    created          datetime default CURRENT_TIMESTAMP not null,
    tags             text                               null,
    decimal_number   varchar(32)                        null,
    contractor_id    bigint                             null,
    registrar_id     bigint                             null,
    version          varchar(128)                       null,
    equipment_id     bigint                             null,
    is_approved      bit      default b'1'              null,
    execution_type   int                                null,
    state            int      default 1                 not null,
    approved_by_id   bigint                             null,
    approval_date    datetime                           null,
    constraint decimal_number
        unique (decimal_number),
    constraint inventory_number
        unique (inventory_number),
    constraint document_approved_by_id_fk
        foreign key (approved_by_id) references person (id),
    constraint document_contractor_id_fk
        foreign key (contractor_id) references person (id),
    constraint document_equipment_id_fk
        foreign key (equipment_id) references equipment (id),
    constraint document_registrar_id_fk
        foreign key (registrar_id) references person (id),
    constraint documentation_type_id_fk
        foreign key (type_id) references document_type (id),
    constraint fk_document_project_id
        foreign key (project_id) references case_object (ID)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index document_decimal_number_id_fk
    on document (decimal_number);

create table if not exists document_member
(
    id          bigint auto_increment
        primary key,
    document_id bigint not null,
    person_id   bigint not null,
    constraint fk_document_id_to_document
        foreign key (document_id) references document (id)
            on update cascade on delete cascade,
    constraint fk_person_id_to_person
        foreign key (person_id) references person (id)
            on update cascade on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create index ix_person_company
    on person (company_id);

create index ix_person_dname
    on person (displayname);

create index ix_person_old_id
    on person (old_id);

create table if not exists platform
(
    id         bigint auto_increment
        primary key,
    company_id bigint       null,
    name       varchar(128) not null,
    parameters varchar(256) null,
    comment    mediumtext   null,
    manager_id bigint       null,
    case_id    bigint       null,
    project_id bigint       null,
    constraint project_id
        unique (project_id),
    constraint fk_case_id_to_case_object
        foreign key (case_id) references case_object (ID)
            on update cascade on delete set null,
    constraint fk_project_id_to_case_object
        foreign key (project_id) references case_object (ID)
            on update cascade,
    constraint platform_company_fk
        foreign key (company_id) references company (id)
            on update cascade on delete cascade,
    constraint platform_person_fk
        foreign key (manager_id) references person (id)
            on update cascade on delete set null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

alter table case_object
    add constraint platform_id_to_platform_fk
        foreign key (platform_id) references platform (id)
            on delete set null;

create table if not exists company_subscription
(
    id          bigint auto_increment
        primary key,
    company_id  bigint      not null,
    email_addr  varchar(64) not null,
    lang_code   varchar(16) null,
    dev_unit_id bigint      null,
    platform_id bigint      null,
    constraint fk_company_subscription
        foreign key (company_id) references company (id),
    constraint fk_companysubscription_dev_unit_id_to_dev_unit
        foreign key (dev_unit_id) references dev_unit (ID)
            on update cascade on delete cascade,
    constraint fk_companysubscription_platform_id_to_platform
        foreign key (platform_id) references platform (id)
            on update cascade on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists project_sla
(
    id                      bigint auto_increment
        primary key,
    importance_level_id     int    null,
    reaction_time           bigint null,
    temporary_solution_time bigint null,
    full_solution_time      bigint null,
    project_id              bigint null,
    constraint `project_sla-case_object-id-fk`
        foreign key (project_id) references case_object (ID)
            on update cascade on delete cascade,
    constraint `project_sla-importance_level-id-fk`
        foreign key (importance_level_id) references importance_level (id)
            on update cascade
);

create table if not exists project_to_product
(
    project_id bigint not null,
    product_id bigint not null,
    primary key (project_id, product_id),
    constraint project_to_product_product_id_fk
        foreign key (product_id) references dev_unit (ID),
    constraint project_to_product_project_id_fk
        foreign key (project_id) references case_object (ID)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists redmine_priority_map
(
    id   bigint auto_increment
        primary key,
    name varchar(128) null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists redmine_priority_map_entry
(
    id                  bigint auto_increment
        primary key,
    MAP_ID              bigint       null,
    RM_priority_id      bigint       null,
    LOCAL_priority_id   bigint       null,
    LOCAL_priority_name varchar(128) null,
    RM_priority_name    varchar(256) null,
    constraint FK_RDMINE_EP_PRIORITY_ENTRY
        foreign key (MAP_ID) references redmine_priority_map (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists redmine_status_map
(
    id   bigint auto_increment
        primary key,
    name varchar(128) null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists redmine_endpoint
(
    id                    bigint auto_increment
        primary key,
    server_addr           varchar(64)  not null,
    project_id            varchar(128) not null,
    api_key               varchar(128) not null,
    COMPANY_ID            bigint       not null,
    last_created          datetime     null,
    last_updated          datetime     null,
    STATUS_MAP_ID         bigint       null,
    PRIORITY_MAP_ID       bigint       null,
    DEFAULT_USER_ID       bigint       null,
    DEFAULT_USER_LOCAL_ID bigint       null,
    constraint FK_RDMINE_EP_COMPANY
        foreign key (COMPANY_ID) references company (id)
            on delete cascade,
    constraint FK_RDMINE_EP_PRIORITY
        foreign key (PRIORITY_MAP_ID) references redmine_priority_map (id)
            on delete cascade,
    constraint FK_RDMINE_EP_STATUS
        foreign key (STATUS_MAP_ID) references redmine_status_map (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists redmine_status_map_entry
(
    id                       bigint auto_increment
        primary key,
    MAP_ID                   bigint       null,
    RM_status_id             bigint       null,
    LOCAL_status_id          int          null,
    LOCAL_status_name        varchar(128) null,
    LOCAL_previous_status_id int          null,
    constraint FK_RDMINE_EP_STATUS_ENTRY
        foreign key (MAP_ID) references redmine_status_map (id)
            on delete cascade,
    constraint FK_RDMINE_TO_CRM_STATUS
        foreign key (LOCAL_status_id) references case_state (ID),
    constraint FK_RDMINE_TO_OLD_CRM_STATUS
        foreign key (LOCAL_previous_status_id) references case_state (ID)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists redmine_to_crm_status_map_entry
(
    id              bigint auto_increment
        primary key,
    MAP_ID          bigint null,
    RM_status_id    bigint null,
    LOCAL_status_id int    null,
    constraint FK_RDMINE_TO_CRM_BACKWARD_STATUS
        foreign key (LOCAL_status_id) references case_state (ID),
    constraint FK_RDMINE_TO_CRM_EP_STATUS_ENTRY
        foreign key (MAP_ID) references redmine_status_map (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists report
(
    id             bigint auto_increment
        primary key,
    name           text                               null,
    status         varchar(16)                        not null,
    case_query     varchar(8192)                      not null,
    creator        bigint                             not null,
    created        datetime                           not null,
    modified       datetime                           null,
    locale         varchar(16) default 'ru'           not null,
    type           varchar(32) default 'CASE_OBJECTS' not null,
    is_restricted  bit         default b'0'           null,
    scheduled_type varchar(16) default 'NONE'         not null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists server
(
    id          bigint auto_increment
        primary key,
    platform_id bigint       not null,
    name        varchar(128) null,
    ip          varchar(64)  null,
    parameters  varchar(256) null,
    comment     mediumtext   null,
    constraint server_platform_fk
        foreign key (platform_id) references platform (id)
            on update cascade on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists application
(
    id          bigint auto_increment
        primary key,
    server_id   bigint        not null,
    name        varchar(128)  null,
    comment     mediumtext    null,
    paths       varchar(2048) null,
    dev_unit_id bigint        null,
    constraint application_dev_unit_fk
        foreign key (dev_unit_id) references dev_unit (ID)
            on update cascade on delete set null,
    constraint application_server_fk
        foreign key (server_id) references server (id)
            on update cascade on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists user_login
(
    id            bigint auto_increment
        primary key,
    ulogin        varchar(64)                        not null,
    upass         varchar(64)                        null,
    created       datetime default CURRENT_TIMESTAMP not null,
    lastPwdChange datetime                           null,
    pwdExpired    datetime                           null,
    astate        int                                not null,
    personId      bigint                             null,
    authType      int                                not null,
    ipMaskAllow   bigint                             null,
    ipMaskDeny    bigint                             null,
    info          varchar(1000)                      null,
    constraint uq_userlogin
        unique (ulogin),
    constraint FK_ULOGIN_PERSON
        foreign key (personId) references person (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists case_filter
(
    id       bigint auto_increment
        primary key,
    name     varchar(256)                       not null,
    login_id bigint                             not null,
    params   json                               null,
    type     varchar(32) default 'CASE_OBJECTS' not null,
    constraint case_filter_login_id_fk
        foreign key (login_id) references user_login (id)
            on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index issue_filter_login_id_fk
    on case_filter (login_id);

create table if not exists user_case_assignment
(
    id           bigint auto_increment
        primary key,
    login_id     bigint        not null,
    table_entity int           not null,
    states       varchar(2048) null,
    persons      varchar(2048) null,
    constraint fk_user_case_assignment_login_id_to_user_login
        foreign key (login_id) references user_login (id)
            on update cascade on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create table if not exists user_dashboard
(
    id             bigint auto_increment
        primary key,
    login_id       bigint       not null,
    case_filter_id bigint       not null,
    name           varchar(256) not null,
    constraint fk_case_filter_id_to_case_filter
        foreign key (case_filter_id) references case_filter (id)
            on update cascade on delete cascade,
    constraint fk_login_id_to_login
        foreign key (login_id) references user_login (id)
            on update cascade on delete cascade
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;;

create index FK_ULOGIN_ASTATE
    on user_login (astate);

create index fk_ulogin_authtype
    on user_login (authType);

create index ix_ulogin_person
    on user_login (personId);

create index uq_user_login
    on user_login (ulogin);

create table if not exists user_role
(
    id                     int auto_increment
        primary key,
    role_code              varchar(256)                   null,
    role_info              varchar(1000)                  null,
    privileges             text                           null,
    scopes                 varchar(1024) default 'SYSTEM' not null,
    is_default_for_contact bit           default b'0'     null
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists login_role_item
(
    id       bigint auto_increment
        primary key,
    login_id bigint not null,
    role_id  int    not null,
    constraint UQ_LR_ITEM
        unique (login_id, role_id),
    constraint FK_LR_LOGIN
        foreign key (login_id) references user_login (id)
            on delete cascade,
    constraint FK_LR_ROLE
        foreign key (role_id) references user_role (id)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create index uq_role_code
    on user_role (role_code);

create table if not exists worker_position
(
    id         bigint auto_increment
        primary key,
    pos_name   varchar(256) not null,
    company_id bigint       not null,
    constraint FK_COMPANY_POS
        foreign key (company_id) references company (id)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

create table if not exists worker_entry
(
    id           bigint auto_increment
        primary key,
    created      datetime default CURRENT_TIMESTAMP not null,
    personId     bigint                             not null,
    dep_id       bigint                             not null,
    companyId    bigint                             not null,
    positionId   bigint                             not null,
    hireDate     datetime                           null,
    hireOrderNo  varchar(256)                       null,
    active       int                                not null,
    worker_extId varchar(32)                        not null,
    constraint FK_WORKER_PE
        foreign key (personId) references person (id)
            on delete cascade,
    constraint FK_WORKER_PE_COMP
        foreign key (companyId) references company (id),
    constraint FK_WORKER_PE_DEP
        foreign key (dep_id) references company_dep (id),
    constraint FK_WORKER_POS
        foreign key (positionId) references worker_position (id)
)
    ENGINE = INNODB
    CHARACTER SET UTF8
    COLLATE utf8_general_ci;

alter table company_dep
    add constraint FK_CD_HEAD
        foreign key (head_id) references worker_entry (id)
            on delete set null;

create index ix_worker_comp
    on worker_entry (companyId);

create index ix_worker_entry
    on worker_entry (personId);

create index ix_worker_pos
    on worker_entry (positionId);

create index uq_ext_worker
    on worker_entry (companyId, worker_extId);
