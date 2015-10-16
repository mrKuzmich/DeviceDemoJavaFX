if defined ProgramFiles(x86) (
	set arch=x64
) else (
	set arch=x86
)
	java -cp "lib\RXTXcomm.jar" -Djava.library.path=lib\%arch% -jar DeviceDemoJavaFX.jar
