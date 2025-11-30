### IMPORTANT
first get Get-Content $env:USERPROFILE\.emulator_console_auth_token
then telnet localhost 5554 (or other emulator port)
auth {token}
redir add udp:9001:8888