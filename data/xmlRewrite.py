import re

#configuration options
DATA_TAG_RENAME = "Entity"
FILE_NAME = "game_trailers.xml"

xml ="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<Data>\n"
with open(FILE_NAME,"r") as xmlFile:
    for line in xmlFile:
        versionTag = re.compile(r"<\?xml version[^>]*\?>")
        line=versionTag.sub("",line)
        line=line.replace("<Data>","<"+DATA_TAG_RENAME+">")
        line=line.replace("</Data>","</"+DATA_TAG_RENAME+">")
        xml+=line
xml +="</Data>"

with open("new_"+FILE_NAME,"w") as xmlFile:
    xmlFile.write(xml)
