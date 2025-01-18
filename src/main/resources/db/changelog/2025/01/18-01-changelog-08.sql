-- liquibase formatted sql

-- changeset a_cha:1726477659739-13
ALTER TABLE datasourceerrorlog
    ADD stack_trace TEXT;
ALTER TABLE datasourceerrorlog
    ADD message TEXT;
ALTER TABLE datasourceerrorlog
    ADD method_signature VARCHAR(30);
