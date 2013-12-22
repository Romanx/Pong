# For Unix and cygwin environment
DEPRECATED=-Xlint:deprecation 
DEPRECATED=
FLAGS=
FLAGS= -source 1.7 -Xlint:unchecked
FLAGS= -source 1.7

compile:	
	javac $(DEPRECATED) $(FLAGS)       \
                            client/*.java  \
                            server/*.java  \
                            common/*.java     

clean:
	rm -f */*.class */*.bak *.bak *.tgz
	rm -f -r html
	rm -f 00text 00text.rtf 000text 000text.rtf
	rm -f -r doc/*.* doc/*
	rm -f Clients/__SHEL*
	rm -f _list.sh

documentation:
	echo "Making documentation"
	javadoc -sourcepath $(WIN_SDK1)\\src.zip         \
	-group Pong Pong                                 \
	-header "<FONT color="teal">Pong</FONT>"         \
	-author -windowtitle "Pong netwoked" \
	-use -splitindex -d html \
	-package client/*.java server/*.java common/*.java

tgz:
	tar cvfz pong.tgz client/*.java server/*.java common/*.java Makefile

run:
	java server/Server &
	sleep 1
	java client/Client &
	java client/Client &
