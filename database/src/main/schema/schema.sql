# CatFood OrientDB schema setup script.
CREATE DATABASE plocal:${project.buildDir}/catfood/orientdb/databases/CatFood admin admin
CONNECT plocal:${project.buildDir}/catfood/orientdb/databases/CatFood admin admin;
#CREATE CLASS Hunk ABSTRACT;
#CREATE CLASS Content EXTENDS Hunk;
#CREATE PROPERTY Content.text STRING (MANDATORY TRUE);
#CREATE CLASS Markup EXTENDS Hunk;
#CREATE PROPERTY Markup.attributes EMBEDDEDMAP STRING;
#CREATE PROPERTY Markup.name STRING (MANDATORY TRUE);
#CREATE PROPERTY Markup.children LINKMAP Hunk;

CREATE CLASS Topic;
CREATE PROPERTY Topic.name STRING (MANDATORY TRUE);
CREATE PROPERTY Topic.lang STRING (MANDATORY TRUE);
CREATE PROPERTY Topic.content STRING;

INSERT INTO Topic (name,lang,content) VALUES('Home','MARKDOWN','This is the home topic');
INSERT INTO Topic (name,lang,content) VALUES('TestTopic','MARKDOWN','# This is some stuff to test with');