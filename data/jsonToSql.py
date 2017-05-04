import json

#configuration options
FILE_NAME= "trailers.json"
TABLE = "games"
NEW_COLUMN = "url"
NEW_COLUMN_TYPE=NEW_COLUMN+" VARCHAR(511)"
JSON_KEY="name"


data = None
with open(FILE_NAME) as jfile:
    data = json.load(jfile)

if data!=None:
    with open(FILE_NAME+".sql","w") as sql:
        sql.write("ALTER TABLE "+TABLE+"\n   ADD "+NEW_COLUMN_TYPE+";\n")
        for key in data.keys():
            if len(data[key]) > 0:
                sql.write("UPDATE "+TABLE+"\n   SET "+NEW_COLUMN+" = 'www.youtube.com/watch?v="+data[key][0]
                    +"'\n   WHERE "+JSON_KEY+" = '"+key.replace("'","\\'")+"';\n")
