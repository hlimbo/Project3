import pymysql
import random

FILE_NAME= "founded.sql"
TABLE = "publishers"
NEW_COLUMN = "founded"
NEW_COLUMN_TYPE=NEW_COLUMN+" YEAR"

random.seed()

fieldNameIndex = 0
conn = pymysql.Connect(host="127.0.0.1",user="atlas",database="gamedb")
curs = conn.cursor()
curs.execute("SELECT id FROM "+TABLE)
pubs=[]
for line in curs:
    pubs.append(line[0])
conn.close()

with open(FILE_NAME,"w") as sql:
    sql.write("ALTER TABLE "+TABLE+"\n   ADD "+NEW_COLUMN_TYPE+";\n")
    for pub_id in pubs:
        if (random.randint(0,1) == 1):
            rand = "19"+str(random.randint(0,2)+7)+str(random.randint(0,9))
        else:
            rand = "200"+str(random.randint(0,6))

        sql.write("UPDATE "+TABLE+"\n   SET "+NEW_COLUMN+" = "+rand
                    +"\n   WHERE id  = "+str(pub_id)+";\n")
