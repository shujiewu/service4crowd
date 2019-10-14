Use service4crowd;

    create table SERVICE_REGISTRATION (
       id bigint not null,
        object_Version bigint,
        default_Version bit,
        metadata_Uri longtext,
        name varchar(255),
        type integer,
        uri longtext,
        version varchar(255),
        primary key (id)
    );

CREATE TABLE SERVICE_DEPLOYMENT  (
	DEPLOYMENT_KEY VARCHAR(255) NOT NULL PRIMARY KEY,
	DEPLOYMENT_ID VARCHAR(255) NOT NULL
)ENGINE=InnoDB;



