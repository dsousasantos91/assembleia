drop table if exists assembleia cascade;
drop table if exists associado cascade;
drop table if exists endereco cascade;
drop table if exists pauta cascade;
drop table if exists sessao cascade;
drop table if exists sessao_associados cascade;
drop table if exists voto cascade;

create table assembleia
(
    id                        bigserial    not null,
    cooperativa               varchar(50),
    data_hora_fim_apuracao    timestamp    not null,
    data_hora_inicio_apuracao timestamp    not null,
    presidente                varchar(50),
    secretario                varchar(50),
    tipo_assembleia           varchar(255) not null,
    primary key (id)
);
create table associado
(
    id   bigserial not null,
    cpf  varchar(11),
    nome varchar(50),
    primary key (id)
);
create table endereco
(
    id            bigserial    not null,
    bairro        varchar(50),
    cep           varchar(8),
    cidade        varchar(50),
    complemento   varchar(255),
    logradouro    varchar(100),
    uf            varchar(255) not null,
    assembleia_id int8         not null,
    primary key (id)
);
create table pauta
(
    id            bigserial not null,
    descricao     varchar(1000),
    titulo        varchar(50),
    assembleia_id int8,
    primary key (id)
);
create table sessao
(
    id                bigserial not null,
    data_hora_fim     timestamp not null,
    data_hora_inicio  timestamp not null,
    resultado_enviado boolean,
    sessao_privada    boolean,
    pauta_id          int8      not null,
    primary key (id)
);
create table sessao_associados
(
    sessao_id     int8 not null,
    associados_id int8 not null
);
create table voto
(
    id             bigserial    not null,
    data_hora_voto timestamp,
    voto           varchar(255) not null,
    associado_id   int8         not null,
    sessao_id      int8         not null,
    primary key (id)
);

alter table associado
    add constraint UniqueAssociado unique (cpf);
alter table voto
    add constraint UniqueVotoSessaoAssociado unique (sessao_id, associado_id);
alter table endereco
    add constraint FK_endereco_assembleia foreign key (assembleia_id) references assembleia;
alter table pauta
    add constraint FK_pauta_assembleia foreign key (assembleia_id) references assembleia;
alter table sessao
    add constraint FK_sessao_pauta foreign key (pauta_id) references pauta;
alter table sessao_associados
    add constraint FK_sessao_associados_associado foreign key (associados_id) references associado;
alter table sessao_associados
    add constraint FK_sessao_associados_sessao foreign key (sessao_id) references sessao;
alter table voto
    add constraint FK_voto_associado foreign key (associado_id) references associado;
alter table voto
    add constraint FK_voto_sessao foreign key (sessao_id) references sessao;
