drop table if exists assembleia cascade;
drop table if exists assembleia_pautas cascade;
drop table if exists associado cascade;
drop table if exists endereco cascade;
drop table if exists pauta cascade;
drop table if exists sessao cascade;
drop table if exists sessao_associados cascade;
drop table if exists voto cascade;
create table assembleia
(
    id                        bigserial    not null,
    cooperativa               varchar(255),
    data_hora_fim_apuracao    timestamp    not null,
    data_hora_inicio_apuracao timestamp    not null,
    presidente                varchar(50),
    secretario                varchar(50),
    tipo_assembleia           varchar(20) not null,
    primary key (id)
);
create table assembleia_pautas
(
    assembleia_id int8 not null,
    pautas_id     int8 not null
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
    logradouro    varchar(255),
    uf            varchar(2) not null,
    assembleia_id int8         not null,
    primary key (id)
);
create table pauta
(
    id        bigserial not null,
    descricao varchar(500),
    titulo    varchar(255),
    primary key (id)
);
create table sessao
(
    id                bigserial not null,
    data_hora_fim     timestamp not null,
    data_hora_inicio  timestamp not null,
    resultado_enviado boolean,
    sessao_privada     boolean,
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
    voto           varchar(3) not null,
    associado_id   int8         not null,
    sessao_id      int8         not null,
    primary key (id)
);
alter table assembleia_pautas
    add constraint assembleia_pautas_pautaUk unique (pautas_id);
alter table associado
    add constraint UniqueAssociado unique (cpf);
alter table voto
    add constraint UniqueVotoSessaoAssociado unique (sessao_id, associado_id);
alter table assembleia_pautas
    add constraint assembleia_pautas_pautaFk foreign key (pautas_id) references pauta;
alter table assembleia_pautas
    add constraint assembleia_pautas_assembleiaFk foreign key (assembleia_id) references assembleia;
alter table endereco
    add constraint endereco_assembleiaFk foreign key (assembleia_id) references assembleia;
alter table sessao
    add constraint sessao_pautaFk foreign key (pauta_id) references pauta;
alter table sessao_associados
    add constraint sessao_associados_associadoFk foreign key (associados_id) references associado;
alter table sessao_associados
    add constraint sessao_associados_sessaoFk foreign key (sessao_id) references sessao;
alter table voto
    add constraint voto_associadoFk foreign key (associado_id) references associado;
alter table voto
    add constraint voto_sessaoFk foreign key (sessao_id) references sessao;
