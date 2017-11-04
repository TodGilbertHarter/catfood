CONNECT plocal:/home/tharter/projects/dogfood/java/workspace/catfood/orientdb/databases/CatFood admin admin;
#CREATE CLASS Hunk ABSTRACT;
#CREATE CLASS Content EXTENDS Hunk;
#CREATE PROPERTY Content.text STRING (MANDATORY TRUE);
#CREATE CLASS Markup EXTENDS Hunk;
#CREATE PROPERTY Markup.attributes EMBEDDEDMAP STRING;
#CREATE PROPERTY Markup.name STRING (MANDATORY TRUE);
#CREATE PROPERTY Markup.children LINKMAP Hunk;

CREATE CLASS Topic;
CREATE PROPERTY Topic.name STRING (MANDATORY TRUE);
CREATE PROPERTY Topic.content STRING;