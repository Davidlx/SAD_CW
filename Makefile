SRCS=*.java
CLASS_ROOT=.
CP_EXTRA=lib/Scenario-0.6.jar:$(CLASSPATH)

.PHONY: all taint run clean

# Build the project
all: 
	javac -cp $(CP_EXTRA) -g -d $(CLASS_ROOT) $(SRCS)

 # Build it with more warning messages
taint: 
	javac -Xlint:unchecked -Xlint:depreciated -cp $(CP_EXTRA) -g -d $(CLASS_ROOT) $(SRCS)

# Convenience target to run program
run:
	java -ea -cp $(CP_EXTRA) org.scotek.vpl.Cooker

# Same as above but run with -zoomer option
# FIXME zoomer doesn't work on some new JVMs - internal Sun classes changed?
runzoomer:
	java -ea -cp $(CP_EXTRA) org.scotek.vpl.Cooker -zoomer

# Use OneJar to package everything into a single, runnable Jar to give to people
dist:	all
	jar cvfm main/Main.jar cookerManifest.mf org icons
	jar cvfm Cooker.jar boot-manifest.mf com main lib

# Delete generatable files.
clean:
	rm -rf org main/Main.jar Cooker.jar
