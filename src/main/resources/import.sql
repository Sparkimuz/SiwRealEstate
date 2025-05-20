
insert into real_estate_agency (id,address,city) values(1,'Via del Corso 123','Roma');
insert into real_estate_agency (id,address,city) values(2,'Corso Vittorio 10','Milano');
insert into real_estate_agency (id,address,city) values(3,'Via Toledo 45','Napoli');

insert into agent (id,real_estate_agency_id,name,surname,birthdate,url_image) values(1,1,'Luca','Bianchi','1985-06-12','/images/agents/luca.jpg');
insert into agent (id,real_estate_agency_id,name,surname,birthdate,url_image) values(2,1,'Giulia','Rossi','1990-03-25','/images/agents/giulia.jpg');
insert into agent (id,real_estate_agency_id,name,surname,birthdate,url_image) values(3,2,'Marco','Verdi','1982-11-02','/images/agents/marco.jpg');
insert into agent (id,real_estate_agency_id,name,surname,birthdate,url_image) values(4,2,'Francesca','Russo','1988-07-18','/images/agents/francesca.jpg');
insert into agent (id,real_estate_agency_id,name,surname,birthdate,url_image) values(5,3,'Antonio','Esposito','1980-02-01','/images/agents/antonio.jpg');

insert into property (id,agent_id,address,city,price,size,type,url_image) values(1,1,'Via Appia 50','Roma',350000,120,'Appartamento','/images/properties/appia50.jpg');
insert into property (id,agent_id,address,city,price,size,type,url_image) values(2,2,'Viale Libia 200','Roma',500000,180,'Attico','/images/properties/libia200.jpg');
insert into property (id,agent_id,address,city,price,size,type,url_image) values(3,3,'Via Brera 8','Milano',750000,140,'Loft','/images/properties/brera8.jpg');
insert into property (id,agent_id,address,city,price,size,type,url_image) values(4,4,'Corso Como 10','Milano',280000,95,'Bilocale','/images/properties/como10.jpg');
insert into property (id,agent_id,address,city,price,size,type,url_image) values(5,5,'Via Chiaia 150','Napoli',220000,110,'Appartamento','/images/properties/chiaia150.jpg');
insert into property (id,agent_id,address,city,price,size,type,url_image) values(6,5,'Via Posillipo 340','Napoli',1200000,250,'Villa','/images/properties/posillipo340.jpg');
