call cat_defs.bat
echo Run the distributed version: Networking to localhost
start java server/Server multiplex_mode
echo When server finishes setup
pause
start java  client/Client 
start java  client/Client
start java  client/Client 0