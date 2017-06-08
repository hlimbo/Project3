import pymysql
table = "games"
fieldNameIndex = 2
conn = pymysql.Connect(host="127.0.0.1",user="atlas",database="gamedb")
curs = conn.cursor()
curs.execute("SELECT DISTINCT * FROM "+table+" LIMIT 2642")
check = True
with open("queryList.txt","w") as quList:
    for line in curs:
        if check:
            print(line[fieldNameIndex])
            check=False
        value = line[fieldNameIndex]
        if value.strip() != "":
                quList.write(value+"\n")
