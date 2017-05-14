#!/bin/bash

#Bash script that runs java class files

if [ "$1" == "--help" ]; then
	echo "Usage: $0 [ path/to/java_class_file ]"
	echo "e.g. $0 testpackage/TestClass"
	exit 0
fi

if [ $# -ne 1 ]; then
	echo "Usage: $0 [ path/to/java_class_file ]"
	echo "Type $0 --help for more information"
	exit 1
fi

MASTER=$(git rev-parse --show-toplevel | sed 's_^C\:_/c_g')
CLASSPATH=$MASTER"/XML_Parsing/classes"
class_file=$1".class"

#check if file does not exist
#if [ ! -f "$CLASSPATH/$class_file" ]; then
#	echo "file not found: $CLASSPATH/$class_file"
#	exit 1
#fi


printf "all:\n\tjava -cp $CLASSPATH $1" > makefile
make --quiet
rm makefile
